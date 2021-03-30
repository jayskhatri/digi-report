package com.digitalpathology.digi_report.ui.add_report;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.object.MedicalReport;
import com.digitalpathology.digi_report.object.Reports.BloodSugrarLevel;
import com.digitalpathology.digi_report.object.Reports.HaemogramReport;
import com.digitalpathology.digi_report.object.Reports.LiverFunctionTest;
import com.digitalpathology.digi_report.object.Reports.RenalFunctionTests;
import com.digitalpathology.digi_report.object.User;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.digitalpathology.digi_report.utils.RandomValGen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddReports extends Fragment {

    private AddReportsViewModel mViewModel;
    private LinearLayout uploadBtn;
    private ImageView uploadedPic;
    private EditText reportname, reportdate;
    private MedicalReport medicalReport;
    private User user;
    private ConnectionDetector connectionDetector;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    private static final int PICK_IMAGE = 1;
    private final static String TAG = "AddReports";

    public static AddReports newInstance() {
        return new AddReports();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_reports, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AddReportsViewModel.class);
        // TODO: Use the ViewModel

        //hooks
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        uploadBtn = getActivity().findViewById(R.id.btn_upload);
        uploadedPic = getActivity().findViewById(R.id.pic);
        reportdate = getActivity().findViewById(R.id.edittext_report_date);
        reportname = getActivity().findViewById(R.id.edittext_report_name);
        connectionDetector = new ConnectionDetector(getActivity());

        //code to change a hamburger icon
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        uploadBtn.setOnClickListener(v -> selectImage(getContext()));

        CardView addReportBtn = getActivity().findViewById(R.id.btn_add_report);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //read user data from firestore
        DocumentReference docRef = clouddb.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    user = new User(String.valueOf(document.get("uid")), String.valueOf(document.get("name")), String.valueOf(document.get("email")),  String.valueOf(document.get("phone")), Integer.parseInt(String.valueOf(document.get("numberOfReportsUploaded"))));

                } else {
                    Log.d(TAG, "user does not exist");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

//        medicalReport = new MedicalReport();
        addReportBtn.setOnClickListener(v -> {
            //increasing total number of reports
            user.addreport();
            //TODO: We have to check for the report image, report name, report date

            addReport(user, currentUser);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");

                        //Logic to enlarge the image
                        int currentBitmapWidth = selectedImage.getWidth();
                        int currentBitmapHeight = selectedImage.getHeight();

                        int ivWidth = uploadedPic.getWidth();
                        int ivHeight = uploadedPic.getHeight();

                        int newWidth = ivWidth;
                        int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

                        Bitmap newbitMap = Bitmap.createScaledBitmap(selectedImage, newWidth, newHeight, true);
                        uploadedPic.setImageBitmap(newbitMap);

                        //uploadedPic.setImageBitmap(selectedImage);
                        //scaleImage(uploadedPic);

                        uploadBtn.setVisibility(View.GONE);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                uploadBtn.setVisibility(View.GONE);
                                uploadedPic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//                                scaleImage(uploadedPic);
                                cursor.close();
                            }
                        }

                    }
                    break;
            }
        }
    }

    private void selectImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);

            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void addReport(User user, FirebaseUser firebaseUser){
        // TODO: API Process
        /**
         * api process will be done here, it will give everything except the reportname and date
         * For now we are adding dummy data
         */
        if(connectionDetector.isInternetAvailble()) {
            RandomValGen randomValGen = new RandomValGen();

            HaemogramReport haemogramReport = new HaemogramReport(randomValGen.betMinMax(14, 18), randomValGen.betMinMax(4.5f, 5.5f), randomValGen.betMinMax(4000, 10000),
                    randomValGen.betMinMax(1.5f, 4.5f), "gm%", "mill/cmm", "/cmm", "Lakh/cmm");

            BloodSugrarLevel bloodSugrarLevel = new BloodSugrarLevel(randomValGen.betMinMax(70, 140), "mg/dl", randomValGen.betMinMax(0, 0.8f), "mmol/L");

            RenalFunctionTests renalFunctionTests = new RenalFunctionTests(randomValGen.betMinMax(15, 40), randomValGen.betMinMax(8, 23), randomValGen.betMinMax(0.9f, 1.5f), randomValGen.betMinMax(2.5f, 7), "mg/dl");

            LiverFunctionTest liverFunctionTest = new LiverFunctionTest(randomValGen.betMinMax(0.0f, 1.0f), randomValGen.betMinMax(0.0f, 0.25f),
                    randomValGen.betMinMax(0.0f, 0.75f), randomValGen.betMinMax(0, 40), randomValGen.betMinMax(37, 147), randomValGen.betMinMax(0, 40),
                    randomValGen.betMinMax(6, 7.8f), randomValGen.betMinMax(3.5f, 5.0f), randomValGen.betMinMax(2.5f, 2.8f), 1.3f,
                    "mg/dl", "gm/dl", "IU/L", "IU/L", "IU/L");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String ts = sdf.format(new Date());

            medicalReport = new MedicalReport(randomValGen.randomInt(), reportname.getText().toString(), "patientname", "refferedby", reportdate.getText().toString(),
                    18, "sex", "address", 1203, 1234, ts, haemogramReport, bloodSugrarLevel, renalFunctionTests, liverFunctionTest, "conclusion", "advise", "bloodGroup", "pathologistname");

            //adding report to cloud firestore
            clouddb.collection("users").document(firebaseUser.getUid()).set(user);
            clouddb.collection("users").document(firebaseUser.getUid()).collection("reports").document(String.valueOf(medicalReport.getId())).set(medicalReport);//

            //adding report to firebase realtime database
            databaseRef.child(firebaseUser.getUid()).child("/reports").child("/"+medicalReport.getId()).setValue(medicalReport);
        }else{
            Toast.makeText(getActivity(), "Check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private int dpToPx(int dp) {
        float density = getActivity().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}