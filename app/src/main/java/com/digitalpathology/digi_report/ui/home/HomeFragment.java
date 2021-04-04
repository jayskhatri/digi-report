package com.digitalpathology.digi_report.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.ui.add_report.AddReports;
import com.digitalpathology.digi_report.ui.my_acc.MyAccountFragment;
import com.digitalpathology.digi_report.ui.my_reports.MyReportsFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private CardView addreports, myreports, myacc;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addreports = getActivity().findViewById(R.id.cv_add_report);
        myreports = getActivity().findViewById(R.id.cv_my_report);
        myacc = getActivity().findViewById(R.id.cv_my_acc);

        addreports.setOnClickListener(v -> {
            loadFragment(new AddReports());
        });

        myreports.setOnClickListener(v -> {
            loadFragment(new MyReportsFragment());
        });

        myacc.setOnClickListener(v -> {
            loadFragment(new MyAccountFragment());
        });
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.nav_host_fragment, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}