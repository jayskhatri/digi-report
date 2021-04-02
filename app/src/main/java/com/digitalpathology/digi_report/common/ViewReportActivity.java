package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.digitalpathology.digi_report.R;

public class ViewReportActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private final String TAG = "ViewReportActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_report_fragment);

        Intent i = getIntent();
        int id = i.getIntExtra("id", -1);

        //hooks
//        progressBar = findViewById(R.id.progress_bar);

        Log.d(TAG, "id: "+ id);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}