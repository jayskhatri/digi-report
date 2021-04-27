package com.digitalpathology.digi_report.ui.report_added;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.ui.add_report.AddReports;

public class ReportAddedFragment extends Fragment {

    private static final String REPORT_NAME = "REPORT_NAME";
    private static final String REPORT_DATE = "REPORT_DATE";

    private String reportName;
    private String reportDate;
    private TextView reportname, reportdate;
    private CardView addAnotherReport;

    private static final String TAG = "ReportAddedFragment";

    public ReportAddedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportAddedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportAddedFragment newInstance(String param1, String param2) {
        ReportAddedFragment fragment = new ReportAddedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            reportName = getArguments().getString(REPORT_NAME);
            reportDate = getArguments().getString(REPORT_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_added, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        reportname = getActivity().findViewById(R.id.act_report_name);
        reportdate = getActivity().findViewById(R.id.upload_date);
        addAnotherReport = getActivity().findViewById(R.id.btn_add_another_report);

        addAnotherReport.setOnClickListener(v -> {
            loadFragment(new AddReports());
        });

        reportname.setText(reportName);
        reportdate.setText(reportDate);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        Log.d(TAG,"it's getting called");
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}