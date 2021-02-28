package com.digitalpathology.digi_report.ui.my_acc;

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

import com.digitalpathology.digi_report.R;

public class MyAccountFragment extends Fragment {

    private MyAccountViewModel myAccountViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myAccountViewModel =
                new ViewModelProvider(this).get(MyAccountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_account, container, false);

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
    }
}