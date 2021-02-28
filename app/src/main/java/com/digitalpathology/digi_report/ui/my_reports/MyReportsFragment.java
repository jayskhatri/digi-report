package com.digitalpathology.digi_report.ui.my_reports;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalpathology.digi_report.object.*;
import com.digitalpathology.digi_report.R;

import java.util.ArrayList;
import java.util.Objects;

public class MyReportsFragment extends Fragment {

    private MyReportsViewModel myReportsViewModel;

    private RecyclerView reportList;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private TextView noReports;

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

        reportList = getActivity().findViewById(R.id.recycler_view_med_list);
        noReports = (TextView) getActivity().findViewById(R.id.text_view_no_med_report);

        mLayoutManager = new LinearLayoutManager(getActivity());
        reportList.setLayoutManager(mLayoutManager);

    }
}