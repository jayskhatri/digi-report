package com.digitalpathology.digi_report.ui.my_reports;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalpathology.digi_report.DashboardActivity;
import com.digitalpathology.digi_report.adapter.ReportAdapter;
import com.digitalpathology.digi_report.common.LoginActivity;
import com.digitalpathology.digi_report.object.*;
import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyReportsFragment extends Fragment {

    private MyReportsViewModel myReportsViewModel;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private TextView noReports, loadError;
    private User user;

    private ConnectionDetector connectionDetector;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    private final String TAG = "MyReportsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myReportsViewModel =
                new ViewModelProvider(this).get(MyReportsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_reports, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        //code to change a hamburger icon
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        recyclerView = getActivity().findViewById(R.id.recycler_view_med_list);
        noReports = (TextView) getActivity().findViewById(R.id.text_view_no_med_report);
        loadError = getActivity().findViewById(R.id.text_view_error_in_loading);
        connectionDetector = new ConnectionDetector(getActivity());

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //Reading the uploaded reports
        readReports();
    }

    private void readReports(){
        if(connectionDetector.isInternetAvailble()) {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            //read user data from firestore
            CollectionReference collectionRef = clouddb.collection("users").document(currentUser.getUid()).collection("reports");
            collectionRef.orderBy("uploadDate").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<MedicalReport> reports = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        reports.add(documentSnapshot.toObject(MedicalReport.class));
                    }

                    //                task.getResult();
                    if (reports.size() > 0) {
                        Log.d(TAG, "DocumentSnapshot data: " + reports.size());
                        //                    user = new User(String.valueOf(document.get("uid")), String.valueOf(document.get("name")), String.valueOf(document.get("email")),  String.valueOf(document.get("phone")));

                        recyclerView.setVisibility(View.VISIBLE);
                        mAdapter = new ReportAdapter(reports, getActivity());
                        recyclerView.setAdapter(mAdapter);
                        noReports.setVisibility(View.GONE);
                    } else if(reports.size() == 0){
                        recyclerView.setVisibility(View.GONE);
                        noReports.setVisibility(View.VISIBLE);
                    }else {
                        Log.d(TAG, "report does not exist");

                        recyclerView.setVisibility(View.GONE);
                        loadError.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }else {
            Toast.makeText(getActivity(), "Internet Not Available", Toast.LENGTH_SHORT).show();
        }
    }
}