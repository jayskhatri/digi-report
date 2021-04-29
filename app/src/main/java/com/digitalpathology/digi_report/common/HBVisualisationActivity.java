package com.digitalpathology.digi_report.common;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.digitalpathology.digi_report.utils.MyMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.grpc.internal.LogExceptionRunnable;

public class HBVisualisationActivity extends AppCompatActivity {

    private LineChart mChart;
    private ConnectionDetector connectionDetector;
    private ArrayList<Entry> xHB;
    private ArrayList<String> yYear;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private long reference_timestamp = 1451660400;
    private final static String TAG = HBVisualisationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_visualisation);

        mChart = findViewById(R.id.barChart_view);
        connectionDetector = new ConnectionDetector(this);

        //chart characteristics
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getDescription().setText("This graph shows historic Haemoglobin data");
        //setting marker
        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);

        renderData();

    }

    public void renderData() {
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
//        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.setAxisMaximum(2021f);
//        xAxis.setAxisMinimum(2016f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
//                Log.d(TAG, "getFormattedValue: value: "+value);
                long convertedTS = (long) value;
                long actTS = convertedTS + reference_timestamp;
                return sdf.format(new Date(actTS));
            }
        });
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(16f, "Maximum Limit");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        LimitLine ll2 = new LimitLine(12f, "Minimum Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(20f);
        leftAxis.setAxisMinimum(0f);
//        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);
        setData();
    }

    private void setData() {

        ArrayList<Entry> values = new ArrayList<>();
//        try {
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2015").getTime(), 9.1f));
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2016").getTime(), 11.2f));
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2017").getTime(), 12.3f));
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2018").getTime(), 13.4f));
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2019").getTime(), 15.6f));
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2020").getTime(), 7.8f));
//            values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse("28/12/2021").getTime(), 8.9f));
////            values.add(new Entry(9, 10.2f));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        for(Entry value : values){
            Log.d(TAG, "setData manual  " + value.toString());
        }

        if(connectionDetector.isInternetAvailble()) {
            CollectionReference collectionRef = clouddb.collection("users").document(currentUser.getUid()).collection("reports");
            collectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String reportDate = document.getString("reportDate");
                        Date date1;
                        try {
//                            long newTS = Objects.requireNonNull(new SimpleDateFormat("dd/MM/yyyy").parse(reportDate)).getTime() - reference_timestamp;
                            Map mapHaemo = (Map) document.get("haemogramReport");
                            Map haemoValue = (Map) mapHaemo.get("values");
                            if (!haemoValue.get("haemoglobin").toString().equals("null")) {
                                values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse(reportDate).getTime(), Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin")))));
//                                yYear.add(reportDate);
                                Log.d(TAG, "setData year: " + reportDate + ", ts: "+ new SimpleDateFormat("dd/MM/yyyy").parse(reportDate).getTime() + ", hb: " + Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin"))));
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "setData: "+ e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    for(Entry entry : values)
                    {
                        Log.d(TAG, "setData firebase " + entry.toString());
                    }

                    //sorting to x values
                    Collections.sort(values, new EntryXComparator());

                    LineDataSet set1;
                    if (mChart.getData() != null &&
                            mChart.getData().getDataSetCount() > 0) {
                        set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                        Log.d(TAG, "setData: size: "+ values.size());
                        set1.setValues(values);
                        mChart.getData().notifyDataChanged();
                        mChart.notifyDataSetChanged();
                    } else {
                        set1 = new LineDataSet(values, "Haemoglobin");
                        set1.setDrawIcons(false);
                        set1.enableDashedLine(10f, 5f, 0f);
                        set1.enableDashedHighlightLine(10f, 5f, 0f);
                        set1.setColor(Color.DKGRAY);
                        set1.setCircleColor(Color.DKGRAY);
                        set1.setLineWidth(1f);
                        set1.setCircleRadius(3f);
                        set1.setDrawCircleHole(false);
                        set1.setValueTextSize(9f);
                        set1.setDrawFilled(true);
                        set1.setFormLineWidth(1f);
                        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                        set1.setFormSize(15.f);

                        if (Utils.getSDKInt() >= 18) {
                            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
                            set1.setFillDrawable(drawable);
                        } else {
                            set1.setFillColor(Color.DKGRAY);
                        }
                        Log.d(TAG, "setData: " + set1.toSimpleString());
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(set1);
                        LineData data = new LineData(dataSets);
                        mChart.setData(data);
                        mChart.invalidate();

                        mChart.getData().notifyDataChanged();
                        mChart.notifyDataSetChanged();
                    }

                }
            });
        }


    }

}