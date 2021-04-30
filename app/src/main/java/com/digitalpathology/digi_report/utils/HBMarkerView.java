package com.digitalpathology.digi_report.utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.digitalpathology.digi_report.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HBMarkerView extends MarkerView {

    private TextView tvContent, tvDate;
    private final String TAG = HBMarkerView.class.getSimpleName();

    public HBMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = (TextView) findViewById(R.id.tvContent);
        tvDate = findViewById(R.id.tvContentDate);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText("HB: " + ce.getHigh());
            tvContent.setText(""+ new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date((long) ce.getX())));
//            Log.d(TAG, "refreshContent1: " + new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date((long) ce.getX())));
        } else {

            tvContent.setText("HB: " + e.getY());
            tvDate.setText("" + new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date((long) e.getX())));
//            Log.d(TAG, "refreshContent: " + new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date((long) e.getX())));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
