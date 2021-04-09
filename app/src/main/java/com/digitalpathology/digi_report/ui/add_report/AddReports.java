package com.digitalpathology.digi_report.ui.add_report;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalpathology.digi_report.DashboardActivity;
import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.api.HttpHandler;
import com.digitalpathology.digi_report.object.MedicalReport;
import com.digitalpathology.digi_report.object.Reports.BloodSugrarLevel;
import com.digitalpathology.digi_report.object.Reports.HaemogramReport;
import com.digitalpathology.digi_report.object.Reports.LiverFunctionTest;
import com.digitalpathology.digi_report.object.Reports.RenalFunctionTests;
import com.digitalpathology.digi_report.object.User;
import com.digitalpathology.digi_report.ui.report_added.ReportAddedFragment;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.digitalpathology.digi_report.utils.RandomValGen;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddReports extends Fragment implements DatePickerDialog.OnDateSetListener {

    // http://100.26.239.59:8080/upload

    private AddReportsViewModel mViewModel;
    private LinearLayout uploadBtn;
    private ImageView uploadedPic;
    private String picturePath = "";
    private EditText reportname, reportdate;
    private MedicalReport medicalReportFromAPI;
    private CardView cvReportDate;
    private User user;
    private ConnectionDetector connectionDetector;

    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;

    private static final int PICK_IMAGE = 1;
    private static final int REQUEST_PERMISSION = 674;
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
        cvReportDate = getActivity().findViewById(R.id.cv_report_date);
        connectionDetector = new ConnectionDetector(getActivity());

        TextView title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("Add Reports");

        storageRef = storage.getReferenceFromUrl("gs://digi-report-i67450jk.appspot.com");

        DatePickerDialog.OnDateSetListener datepickerlistener = (view, year, month, dayOfMonth) -> {
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            reportdate.setText(sdf.format(c.getTime()));
        };

        //date choose
        reportdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), datepickerlistener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.setCancelable(false);
            datePickerDialog.setTitle("Select date");
            datePickerDialog.show();
        });

        //code to change a hamburger icon
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        uploadBtn.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }

            selectImage(getContext());

        });

        CardView addReportBtn = getActivity().findViewById(R.id.btn_add_report);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //read user data from firestore
        DocumentReference docRef = clouddb.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
//                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    user = new User(String.valueOf(document.get("uid")), String.valueOf(document.get("name")), String.valueOf(document.get("email")),  String.valueOf(document.get("phone")), Integer.parseInt(String.valueOf(document.get("numberOfReportsUploaded"))));

                } else {
                    Log.d(TAG, "user does not exist");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        addReportBtn.setOnClickListener(v -> {
            if(validateFields(reportname.getText().toString(), reportdate.getText().toString(), uploadedPic)){
                //increasing total number of reports
                user.addreport();
                addReport(user, currentUser);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap selectedImage;
        if(resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImage = (Bitmap) data.getExtras().get("data");

                        //Logic to enlarge the image
                        int currentBitmapWidth = selectedImage.getWidth();
                        int currentBitmapHeight = selectedImage.getHeight();

                        int ivWidth = uploadedPic.getWidth();
                        int ivHeight = uploadedPic.getHeight();

                        int newWidth = ivWidth;
                        int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

                        Bitmap newbitMap = Bitmap.createScaledBitmap(selectedImage, newWidth, newHeight, true);
                        uploadedPic.setImageBitmap(newbitMap);
                        uploadedPic.setTag("uploaded");

                        //uploadedPic.setImageBitmap(selectedImage);
                        //scaleImage(uploadedPic);

                        uploadBtn.setVisibility(View.GONE);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImageUri =  data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImageUri != null) {
                            Cursor cursor = getActivity().getContentResolver().query(selectedImageUri,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                picturePath = cursor.getString(columnIndex);

                                selectedImage = BitmapFactory.decodeFile(picturePath);
                                //Logic to enlarge the image
                                int currentBitmapWidth = selectedImage.getWidth();
                                int currentBitmapHeight = selectedImage.getHeight();

                                int ivWidth = uploadedPic.getWidth();
                                int ivHeight = uploadedPic.getHeight();

                                int newWidth = ivWidth;
                                int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

                                Bitmap newbitMap = Bitmap.createScaledBitmap(selectedImage, newWidth, newHeight, true);
                                try {
                                    uploadedPic.setImageBitmap(newbitMap);

//                                    Bitmap bm = BitmapFactory.decodeFile(picturePath);
//                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                    bm.compress(Bitmap.CompressFormat.JPEG, 25, baos); // bm is the bitmap object
//                                    byte[] b = baos.toByteArray();
                                    GetAddedReportData get = new GetAddedReportData(getContext(), (AppCompatActivity) getActivity());
                                    get.execute(picturePath);

                                    Log.d(TAG, "success");
                                }catch (Exception e){
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    options.inSampleSize = 2;
                                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);
                                    Log.d(TAG, "failure: " + Arrays.toString(e.getStackTrace()));
                                    uploadedPic.setImageBitmap(bitmap);

                                }
                                uploadBtn.setVisibility(View.GONE);
                                uploadedPic.setTag("uploaded");
                                cursor.close();
                            }
                        }else  Log.d(TAG, "else beta");

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
//            if (options[item].equals("Take Photo")) {
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(takePicture, 0);
//
//            } else
                if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    String uploadImage(ImageView imageView, User user, FirebaseUser firebaseUser){
        storageRef = FirebaseStorage.getInstance().getReference(user.getUid() + "/reports/"+ "report" + user.getNumberOfReportsUploaded() + ".png");

        final String[] url = {""};
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_progressbar);

        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progress_horizontal);
        final TextView text2 = dialog.findViewById(R.id.value123);

//        Uri file = Uri.fromFile(new File(imagePath));
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        UploadTask uploadTask = storageRef.putBytes(data, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            Log.d(TAG, "Upload is " + progress + "% done");
            progressBar.setProgress((int) progress);
//            text2.setText((int) progress);

            if(progress == 100.0)
                dialog.dismiss();
        }).addOnPausedListener(taskSnapshot -> Log.d(TAG, "Upload is paused"))
         .addOnFailureListener(exception -> {
             // Handle unsuccessful uploads
         }).addOnSuccessListener(taskSnapshot -> {
             storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                 Log.d(TAG, "uri: ::: " + uri.toString());
             });
        });
        return  storageRef.toString();
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
                    randomValGen.betMinMax(1.5f, 4.5f), "gm%", "mill/cmm", "/cmm", "Lakh/cmm", randomValGen.betMinMax(40f, 70f), randomValGen.betMinMax(20f, 40f),
                    randomValGen.betMinMax(1f, 6f), randomValGen.betMinMax(2f, 10f), randomValGen.betMinMax(0f,1f), randomValGen.betMinMax(76f, 96f),
                    randomValGen.betMinMax(27f, 31f), randomValGen.betMinMax(32f, 36f), randomValGen.betMinMax(11.5f, 14f), "%", "fl", "pg", "g/dl", "%");
            Log.d(TAG, "haemo: "+ haemogramReport.toString());
            BloodSugrarLevel bloodSugrarLevel = new BloodSugrarLevel(randomValGen.betMinMax(70, 140), "mg/dl", randomValGen.betMinMax(0, 0.8f), "mmol/L");

            RenalFunctionTests renalFunctionTests = new RenalFunctionTests(randomValGen.betMinMax(15, 40), randomValGen.betMinMax(8, 23), randomValGen.betMinMax(0.9f, 1.5f), randomValGen.betMinMax(2.5f, 7), "mg/dl");

            LiverFunctionTest liverFunctionTest = new LiverFunctionTest(randomValGen.betMinMax(0.0f, 1.0f), randomValGen.betMinMax(0.0f, 0.25f),
                    randomValGen.betMinMax(0.0f, 0.75f), randomValGen.betMinMax(0, 40), randomValGen.betMinMax(37, 147), randomValGen.betMinMax(0, 40),
                    randomValGen.betMinMax(6, 7.8f), randomValGen.betMinMax(3.5f, 5.0f), randomValGen.betMinMax(2.5f, 2.8f), 1.3f,
                    "mg/dl", "gm/dl", "IU/L", "IU/L", "IU/L");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String ts = sdf.format(new Date());

            //upload image
            String url = uploadImage(uploadedPic, user, firebaseUser);

            medicalReportFromAPI = new MedicalReport(randomValGen.randomInt(), url, reportname.getText().toString(), "patientname", "refferedby", reportdate.getText().toString(),
                    18, "sex", "address", 1203, 1234, ts, haemogramReport, bloodSugrarLevel, renalFunctionTests, liverFunctionTest, "conclusion", "advise", "bloodGroup", "pathologistname");

            //adding report to cloud firestore
            clouddb.collection("users").document(firebaseUser.getUid()).set(user);
            clouddb.collection("users").document(firebaseUser.getUid()).collection("reports").document(String.valueOf(medicalReportFromAPI.getId())).set(medicalReportFromAPI);//

            //adding report to firebase realtime database
            databaseRef.child(firebaseUser.getUid()).child("/reports").child("/"+medicalReportFromAPI.getId()).setValue(medicalReportFromAPI);
            Fragment fragment = new ReportAddedFragment();
            Bundle bundle = new Bundle();
            bundle.putString("REPORT_NAME", reportname.getText().toString());
            bundle.putString("REPORT_DATE", todayDate());
            fragment.setArguments(bundle);
            loadFragment(fragment);
        }else{
            Toast.makeText(getActivity(), "Check your internet", Toast.LENGTH_SHORT).show();
        }
    }

    private int dpToPx(int dp) {
        float density = getActivity().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    private String todayDate(){
        String strDateFormat = "E, dd MMM yyyy, hh:mm:ss a";
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat(strDateFormat);
        return df.format(date);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.

            } else {
                // User refused to grant permission.
                Toast.makeText(getActivity(), "You need to give the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    private boolean validateFields(String reportName, String reportDate, ImageView img){
        if(reportName.isEmpty()){
            reportname.setError("Please enter valid name of the report");
            return false;
        }
        if (reportDate.isEmpty()){
            reportdate.setError("Please select date of the report");
            return false;
        }
        if(img.getTag() == null){
            Toast.makeText(getActivity(), "Please add report Image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    //async class
    public class GetAddedReportData extends AsyncTask<String, Context, MedicalReport>{

        private Context c;
        private AppCompatActivity activity;
        private String url = "http://100.26.239.59:8080/upload";
        private AlertDialog alertDialog;
        private final String TAG = GetAddedReportData.class.getSimpleName();

        public GetAddedReportData(Context c, AppCompatActivity activity) {
            this.c = c;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            alertDialog  = createDialog(c);
            alertDialog.show();
        }

        @Override
        protected MedicalReport doInBackground(String... strings) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = "{\"advice\":\"\",\"pathologist_2\":\"Dr. Seema Modh MD(Path)\",\"blood_group\":\"\\\"B\\\" POSITIVE\",\"pathologist_1\":\"Dr. Akash Prajapati MD(Path)\",\"patient_name\":\"Isha V. Khimsurya\",\"conclusion\":\"Mild microcytic hypochromic anemia\",\"date\":\"28/12/2017\",\"ref_no\":\"11229\",\"hospital_name\":\"CHARUSAT HOSPITAL\",\"sex\":\"Female\",\"report_name\":\"HAEMOGRAM REPORT\",\"case_no\":\"62263\",\"age\":\"17 Years\",\"referred_by_dr\":\"Dr. Nilam Mehta\",\"haemo_report\":{\"BLOOD COUNTS:\":{\"results\":null,\"units\":null,\"reference_range\":null},\"Haemoglobir\":{\"results\":\"9.45\",\"units\":\"gm%\",\"reference_range\":\"[ M: 14-18,F:12-16]\"},\"R.B.C. Count\":{\"results\":\"6.57\",\"units\":\"mill./c.mm\",\"reference_range\":\"[ M:4.5-5.5,F:3.8-5.2]\"},\"W.B.C. Count\":{\"results\":\"7020\",\"units\":\"/c.mm\",\"reference_range\":\"4000-10000\"},\"Platelet Count\":{\"results\":\"3.07\",\"units\":\"Lakh/cmm\",\"reference_range\":\"1.5-4.5\"},\"DIFFERENTIAL COUNT:\":{\"results\":null,\"units\":null,\"reference_range\":null},\"Polymorphs\":{\"results\":\"66\",\"units\":\"%\",\"reference_range\":\"40 - 70\"},\"Lymphocytes\":{\"results\":\"26\",\"units\":\"%\",\"reference_range\":\"20 - 40\"},\"Eosinophils\":{\"results\":\"01\",\"units\":\"%\",\"reference_range\":\"1 - 6\"},\"Monocytes\":{\"results\":\"07\",\"units\":\"%\",\"reference_range\":\"2 - 10\"},\"Basophils\":{\"results\":\"00\",\"units\":\"%\",\"reference_range\":\"0 - 1\"},\"BLOOD INDICES:\":{\"results\":null,\"units\":null,\"reference_range\":null},\"M.C.V\":{\"results\":\"54.9\",\"units\":\"fl\",\"reference_range\":\"76 - 96\"},\"M.C.H\":{\"results\":\"14.4\",\"units\":\"pg\",\"reference_range\":\"27 - 31\"},\"M.C.H.C.\":{\"results\":\"26.2\",\"units\":\"g/dl\",\"reference_range\":\"32 - 36\"},\"R.D.W\":{\"results\":\"13.4\",\"units\":\"%\",\"reference_range\":\"11.5 - 14\"}}}\n"; //sh.multipartRequest("http://100.26.239.59:8080/upload", null, strings[0], "image", "image/jpeg");
//            "{\"advice\":\"\",\"pathologist_2\":\"Dr. Seema Modh MD(Path)\",\"blood_group\":\"\\\"B\\\" POSITIVE\",\"pathologist_1\":\"Dr. Akash Prajapati MD(Path)\",\"patient_name\":\"Isha V. Khimsurya\",\"conclusion\":\"Mild microcytic hypochromic anemia\",\"date\":\"28/12/2017\",\"ref_no\":\"11229\",\"hospital_name\":\"CHARUSAT HOSPITAL\",\"sex\":\"Female\",\"report_name\":\"HAEMOGRAM REPORT\",\"case_no\":\"62263\",\"age\":\"17 Years\",\"referred_by_dr\":\"Dr. Nilam Mehta\",\"haemo_report\":{\"BLOOD COUNTS:\":{\"results\":null,\"units\":null,\"reference_range\":null},\"Haemoglobir\":{\"results\":\"9.45\",\"units\":\"gm%\",\"reference_range\":\"[ M: 14-18,F:12-16]\"},\"R.B.C. Count\":{\"results\":\"6.57\",\"units\":\"mill./c.mm\",\"reference_range\":\"[ M:4.5-5.5,F:3.8-5.2]\"},\"W.B.C. Count\":{\"results\":\"7020\",\"units\":\"/c.mm\",\"reference_range\":\"4000-10000\"},\"Platelet Count\":{\"results\":\"3.07\",\"units\":\"Lakh/cmm\",\"reference_range\":\"1.5-4.5\"},\"DIFFERENTIAL COUNT:\":{\"results\":null,\"units\":null,\"reference_range\":null},\"Polymorphs\":{\"results\":\"66\",\"units\":\"%\",\"reference_range\":\"40 - 70\"},\"Lymphocytes\":{\"results\":\"26\",\"units\":\"%\",\"reference_range\":\"20 - 40\"},\"Eosinophils\":{\"results\":\"01\",\"units\":\"%\",\"reference_range\":\"1 - 6\"},\"Monocytes\":{\"results\":\"07\",\"units\":\"%\",\"reference_range\":\"2 - 10\"},\"Basophils\":{\"results\":\"00\",\"units\":\"%\",\"reference_range\":\"0 - 1\"},\"BLOOD INDICES:\":{\"results\":null,\"units\":null,\"reference_range\":null},\"M.C.V\":{\"results\":\"54.9\",\"units\":\"fl\",\"reference_range\":\"76 - 96\"},\"M.C.H\":{\"results\":\"14.4\",\"units\":\"pg\",\"reference_range\":\"27 - 31\"},\"M.C.H.C.\":{\"results\":\"26.2\",\"units\":\"g/dl\",\"reference_range\":\"32 - 36\"},\"R.D.W\":{\"results\":\"13.4\",\"units\":\"%\",\"reference_range\":\"11.5 - 14\"}}}\n"; //
            Log.e(TAG, "Response from url: " + jsonStr);

            /*
                {
                    "pathologist_1": "Dr. Akash Prajapati MD(Path)",
                    "pathologist_2": "Dr. Seema Modh MD(Path)",
                    "conclusion": "Mild microcytic hypochromic anemia",
                    "advice": "",
                    "report_name": "HAEMOGRAM REPORT",
                    "blood_group": "\"B\" POSITIVE",
                    "patient_name": "Isha V. Khimsurya",
                    "age": "17 Years",
                    "sex": "Female",
                    "referred_by_dr": "Dr. Nilam Mehta",
                    "hospital_name": "CHARUSAT HOSPITAL",
                    "ref_no": "11229",
                    "date": "28/12/2017",
                    "case_no": "62263",
                    "haemo_report": {
                        "BLOOD COUNTS:": {
                            "results": null,
                            "units": null,
                            "reference_range": null
                        },
                        "Haemoglobir": {
                            "results": "9.45",
                            "units": "gm%",
                            "reference_range": "[ M: 14-18,F:12-16]"
                        },
                        "R.B.C. Count": {
                            "results": "6.57",
                            "units": "mill./c.mm",
                            "reference_range": "[ M:4.5-5.5,F:3.8-5.2]"
                        },
                        "W.B.C. Count": {
                            "results": "7020",
                            "units": "/c.mm",
                            "reference_range": "4000-10000"
                        },
                        "Platelet Count": {
                            "results": "3.07",
                            "units": "Lakh/cmm",
                            "reference_range": "1.5-4.5"
                        },
                        "DIFFERENTIAL COUNT:": {
                            "results": null,
                            "units": null,
                            "reference_range": null
                        },
                        "Polymorphs": {
                            "results": "66",
                            "units": "%",
                            "reference_range": "40 - 70"
                        },
                        "Lymphocytes": {
                            "results": "26",
                            "units": "%",
                            "reference_range": "20 - 40"
                        },
                        "Eosinophils": {
                            "results": "01",
                            "units": "%",
                            "reference_range": "1 - 6"
                        },
                        "Monocytes": {
                            "results": "07",
                            "units": "%",
                            "reference_range": "2 - 10"
                        },
                        "Basophils": {
                            "results": "00",
                            "units": "%",
                            "reference_range": "0 - 1"
                        },
                        "BLOOD INDICES:": {
                            "results": null,
                            "units": null,
                            "reference_range": null
                        },
                        "M.C.V": {
                            "results": "54.9",
                            "units": "fl",
                            "reference_range": "76 - 96"
                        },
                        "M.C.H": {
                            "results": "14.4",
                            "units": "pg",
                            "reference_range": "27 - 31"
                        },
                        "M.C.H.C.": {
                            "results": "26.2",
                            "units": "g/dl",
                            "reference_range": "32 - 36"
                        },
                        "R.D.W": {
                            "results": "13.4",
                            "units": "%",
                            "reference_range": "11.5 - 14"
                        }
                    }
                }
            */

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    String pathologist1 = jsonObj.getString("pathologist_1");
                    String pathologist2 = jsonObj.getString("pathologist_2");
                    String hospitalname = jsonObj.getString("hospital_name");
                    String reportName = jsonObj.getString("report_name");
                    String reportDate = jsonObj.getString("date");
                    String patientName = jsonObj.getString("patient_name");
                    String age = jsonObj.getString("age");
                    String sex = jsonObj.getString("sex");
                    String refno = jsonObj.getString("ref_no");
                    String caseNo = jsonObj.getString("case_no");
                    String referredByDR = jsonObj.getString("referred_by_dr");
                    String conclusion = jsonObj.getString("conclusion");
                    String advise = jsonObj.getString("advice");
                    String bloodGroup = jsonObj.getString("blood_group");

                    Log.d(TAG, "refno: " + refno);

                    JSONObject haemoreport = jsonObj.getJSONObject("haemo_report");
                    Log.d(TAG, haemoreport.toString());
                } catch (JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(MedicalReport medicalReport) {
            super.onPostExecute(medicalReport);

            if(alertDialog.isShowing())
                alertDialog.dismiss();
            medicalReportFromAPI = medicalReport;

        }

        private AlertDialog createDialog(Context context){
            LayoutInflater factory = LayoutInflater.from(context);
            final View dialogView = factory.inflate(R.layout.custom_dialogue,null);
            final AlertDialog processDialog = new AlertDialog.Builder(context).create();
            processDialog.setView(dialogView);
            processDialog.setCancelable(false);
            return processDialog;
        }
    }
}