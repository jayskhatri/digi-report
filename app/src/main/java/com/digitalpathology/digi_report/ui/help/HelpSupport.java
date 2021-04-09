package com.digitalpathology.digi_report.ui.help;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalpathology.digi_report.R;

public class HelpSupport extends Fragment {

    private HelpSupportViewModel mViewModel;
    private TextView title;

    public static HelpSupport newInstance() {
        return new HelpSupport();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_support, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        title.setText("Help & Support");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HelpSupportViewModel.class);
        // TODO: Use the ViewModel

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        //code to change a hamburger icon
        toolbar.post(() -> {
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu28, null);
            toolbar.setNavigationIcon(d);
        });

        TextView title = getActivity().findViewById(R.id.toolbar_title);
        title.setText("Help & Support");
    }

}