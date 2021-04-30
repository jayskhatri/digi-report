package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.digitalpathology.digi_report.R;

public class VisualisationActivity extends AppCompatActivity {

    private CardView hb, rbc, wbc, platelet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualisation);

        //hooks
        hb = findViewById(R.id.cv_hb_visualisation);
        rbc = findViewById(R.id.cv_rbc_visualisation);
        wbc = findViewById(R.id.cv_wbc_visualisation);
        platelet = findViewById(R.id.cv_platelet_visualisation);

        hb.setOnClickListener(v->{
            startActivity(new Intent(VisualisationActivity.this, HBVisualisationActivity.class));
        });

        rbc.setOnClickListener(v->{
            startActivity(new Intent(VisualisationActivity.this, RBCVisualisationActivity.class));
        });

        wbc.setOnClickListener(v->{
            startActivity(new Intent(VisualisationActivity.this, WBCVisualisationActivity.class));
        });

        platelet.setOnClickListener(v->{
            startActivity(new Intent(VisualisationActivity.this, PlateletsVisualisationActivity.class));
        });
    }
}