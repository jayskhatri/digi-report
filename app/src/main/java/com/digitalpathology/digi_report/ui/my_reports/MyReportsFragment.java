package com.digitalpathology.digi_report.ui.my_reports;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.adapter.ReportAdapter;
import com.digitalpathology.digi_report.common.HBVisualisationActivity;
import com.digitalpathology.digi_report.common.ViewReportActivity;
import com.digitalpathology.digi_report.common.VisualisationActivity;
import com.digitalpathology.digi_report.object.MedicalReport;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyReportsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private TextView noReports, loadError, title;
    private Button visualisationBtn;
    private AlertDialog alertDialog;

    private ConnectionDetector connectionDetector;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    private final String TAG = "MyReportsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_reports, container, false);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText("My Reports");
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
        title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("My Reports");

        recyclerView = getActivity().findViewById(R.id.recycler_view_med_list);
        noReports = (TextView) getActivity().findViewById(R.id.text_view_no_med_report);
        loadError = getActivity().findViewById(R.id.text_view_error_in_loading);
        visualisationBtn = getActivity().findViewById(R.id.btn_hb_visualization);
        connectionDetector = new ConnectionDetector(getActivity());

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        //visualization
        visualisationBtn.setOnClickListener(v->openVisualizationActivity());

        //Reading the uploaded reports
        alertDialog = createDialog(getActivity());
        alertDialog.show();
        readReports();
        alertDialog.dismiss();
    }

    private void openVisualizationActivity(){
        Intent i = new Intent(getActivity(), VisualisationActivity.class);
//        i.putExtra("ID", id);
        startActivity(i);
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
                        visualisationBtn.setVisibility(View.VISIBLE);
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

    private AlertDialog createDialog(Context context){
        Log.d(TAG, "createDialog: ");
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.custom_dialogue,null);
        final AlertDialog processDialog = new AlertDialog.Builder(context).create();
        processDialog.setView(dialogView);
        processDialog.setCancelable(false);
        return processDialog;
    }
}