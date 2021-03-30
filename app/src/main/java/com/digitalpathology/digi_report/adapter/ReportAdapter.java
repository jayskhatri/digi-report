package com.digitalpathology.digi_report.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.common.LoginActivity;
import com.digitalpathology.digi_report.object.MedicalReport;
import com.digitalpathology.digi_report.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.MyViewHolder> {

    private List<MedicalReport> reports;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private User user;
    private final String TAG = "ReportAdapter";

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
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView uploadDate, reportName;
        public ImageView deleteReportBtn;

        public MyViewHolder(View view) {
            super(view);
            uploadDate = view.findViewById(R.id.report_uploaddate);
            reportName = view.findViewById(R.id.report_name);
            deleteReportBtn = view.findViewById(R.id.btn_delete_report);
        }
    }



    public ReportAdapter(List<MedicalReport> medicalReports) {
        this.reports = medicalReports;
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
        clouddb.collection("users").document(currentUser.getUid()).set(user1);

        clouddb.collection("users").document(currentUser.getUid())
                .collection("reports").document(String.valueOf(key.getId())).delete().addOnCompleteListener(task -> {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, reports.size());
                    Log.i(TAG,"data removed");
                });
    }

}
