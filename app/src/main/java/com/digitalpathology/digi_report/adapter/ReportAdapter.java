package com.digitalpathology.digi_report.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalpathology.digi_report.DashboardActivity;
import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.common.LoginActivity;
import com.digitalpathology.digi_report.common.ViewReportActivity;
import com.digitalpathology.digi_report.object.MedicalReport;
import com.digitalpathology.digi_report.object.User;
import com.digitalpathology.digi_report.ui.my_reports.MyReportsFragment;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    private List<MedicalReport> reports;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private User user;
    private Context context;
    private String savephotoName;
    private ConnectionDetector connectionDetector;
    private final String TAG = "ReportAdapter";
    private final int REQUEST_PERMISSION = 774;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_report, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MedicalReport report = reports.get(position);
        holder.reportName.setText(report.getReportName());
        holder.uploadDate.setText(report.getReportDate());

        holder.deleteReportBtn.setOnClickListener(v -> {
            removeItem(position, user);
        });

        holder.viewReportBtn.setOnClickListener(v -> {
            Intent i = new Intent(context, ViewReportActivity.class);
            i.putExtra("id", report.getId());
            context.startActivity(i);
//            loadFragment(new MyReportsFragment());
        });

        //download btn
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef = FirebaseStorage.getInstance().getReference(report.getUrl().substring(38));//FirebaseStorage.getInstance().getReferenceFromUrl("gs://digi-report-i67450jk.appspot.com");
        holder.downloadBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION);
            }
            if(connectionDetector.isInternetAvailble()){
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Log.d(TAG, "bytes" + bytes);
                    savephotoName = report.getReportName() + ".png";
                    SavePhotoTask t = new SavePhotoTask();
                    t.execute(bytes);
                    Toast.makeText(context, "Report image saved in downloads folder as "+savephotoName, Toast.LENGTH_LONG).show();
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Toast.makeText(context, "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
                });
            }else {
                Toast.makeText(context, "Internet Unavailable", Toast.LENGTH_SHORT).show();
            }});

        //share btn
        holder.shareBtn.setOnClickListener(v -> {
            if(connectionDetector.isInternetAvailble()){
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Log.d(TAG, "bytes" + bytes);

                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    File photo = new File(Environment.getExternalStorageDirectory() + File.separator + report.getReportName() + ".png");
                    if (photo.exists()) {
                        photo.delete();
                    }

                    try {
                        FileOutputStream fos=new FileOutputStream(photo.getPath());

                        fos.write(bytes);
                        fos.close();
                    }
                    catch (IOException e) {
                        Log.e("PictureDemo", "Exception in photoCallback", e);
                    }

                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+ report.getReportName() + ".png"));
                    context.startActivity(Intent.createChooser(share, "Share Image"));

//                    Toast.makeText(context, "Report image saved in downloads folder as "+savephotoName, Toast.LENGTH_LONG).show();
                }).addOnFailureListener(exception -> {
                    // Handle any errors
                    Toast.makeText(context, "Something went wrong, Please try after sometime", Toast.LENGTH_SHORT).show();
                });
            }else {
                Toast.makeText(context, "Internet Unavailable", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView uploadDate, reportName;
        public ImageView deleteReportBtn, viewReportBtn, shareBtn, downloadBtn;

        public MyViewHolder(View view) {
            super(view);
            connectionDetector = new ConnectionDetector(context);
            uploadDate = view.findViewById(R.id.report_uploaddate);
            reportName = view.findViewById(R.id.report_name);
            downloadBtn = view.findViewById(R.id.btn_download_report);
            shareBtn = view.findViewById(R.id.btn_share_report);
            deleteReportBtn = view.findViewById(R.id.btn_delete_report);
            viewReportBtn = view.findViewById(R.id.btn_view_report);
        }
    }

    public ReportAdapter(List<MedicalReport> medicalReports, Context c) {
        this.reports = medicalReports;
        this.context = c;
        //setting up user
        //read user data from firestore
        DocumentReference docRef = clouddb.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    this.user = new User(String.valueOf(document.get("uid")), String.valueOf(document.get("name")), String.valueOf(document.get("email")),  String.valueOf(document.get("phone")), Integer.parseInt(String.valueOf(document.get("numberOfReportsUploaded"))));
                    Log.d(TAG, "user name: "+user.getName());
                } else {
                    Log.d(TAG, "user does not exist");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    public void removeItem(int position, User user1){

        MedicalReport key = reports.get(position);
        reports.remove(position);

        //updating the number of reports
        user1.removereport();

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(String.valueOf(R.string.shared_pref_total_reports), String.valueOf(user1.getNumberOfReportsUploaded()));
        editor.commit();

        clouddb.collection("users").document(currentUser.getUid()).set(user1);

        clouddb.collection("users").document(currentUser.getUid())
                .collection("reports").document(String.valueOf(key.getId())).delete().addOnCompleteListener(task -> {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, reports.size());
                    Log.i(TAG,"data removed");
                });
    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {
            File photo=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), savephotoName);

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos=new FileOutputStream(photo.getPath());

                fos.write(jpeg[0]);
                fos.close();
            }
            catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            return(null);
        }
    }
}
