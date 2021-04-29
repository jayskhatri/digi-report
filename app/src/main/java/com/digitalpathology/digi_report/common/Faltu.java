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
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Faltu extends AppCompatActivity {

    private LineChart mChart;
    private ConnectionDetector connectionDetector;
    private ArrayList<Entry> xHB;
    private ArrayList<String> yYear;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private final static String TAG = HBVisualisationActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_visualisation);

        mChart = findViewById(R.id.barChart_view);
        connectionDetector = new ConnectionDetector(this);

        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);

        MyMarkerView mv = new MyMarkerView(getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);

        xHB = new ArrayList<Entry>();
        yYear = new ArrayList<String>();

        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getXAxis().setTextSize(15f);
        mChart.getAxisLeft().setTextSize(15f);
        XAxis xl = mChart.getXAxis();
        xl.setValueFormatter(new IndexAxisValueFormatter(){
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            @Override
            public String getFormattedValue(float value) {
                long millis = TimeUnit.HOURS.toMillis((long) value);
                return mFormat.format(new Date(millis));
            }
        });
        xl.setAvoidFirstLastClipping(true);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setInverted(true);
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);

        showData();
    }

    private void showData(){
        if(connectionDetector.isInternetAvailble()) {
            CollectionReference collectionRef = clouddb.collection("users").document(currentUser.getUid()).collection("reports");
            collectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String reportDate = document.getString("reportDate");
                        Date date1;
                        try {
                            date1=new SimpleDateFormat("dd/MM/yyyy").parse(reportDate);
                            Map mapHaemo = (Map) document.get("haemogramReport");
                            Map haemoValue = (Map) mapHaemo.get("values");
                            if(!haemoValue.get("haemoglobin").toString().equals("null")) {
                                xHB.add(new Entry(date1.getTime(), Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin")))));
                                yYear.add(reportDate);
                                Log.d(TAG,"year: " + reportDate +", hb: "+ Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin"))));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            LineDataSet dataSet = new LineDataSet(xHB, "checkit");
            LineData lineData = new LineData(dataSet);
            mChart.setData(lineData);
            dataSet.setColor(Color.RED);
            dataSet.setDrawCircles(false);
            dataSet.setDrawValues(false);
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            mChart.getDescription().setText("");
            mChart.getLegend().setEnabled(false);
            mChart.invalidate();

        }else {
            Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBarChart(ArrayList<Entry> values){
        LineDataSet set1;
        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(values, "Sample Data");
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
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
            mChart.invalidate();
        }
    }

//    public void renderData() {
////        LimitLine llXAxis = new LimitLine(10f, "Index 10");
////        llXAxis.setLineWidth(4f);
////        llXAxis.enableDashedLine(10f, 10f, 0f);
////        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
////        llXAxis.setTextSize(10f);
////
////        XAxis xAxis = mChart.getXAxis();
////        xAxis.enableGridDashedLine(10f, 10f, 0f);
////        xAxis.setAxisMaximum(2021f);
////        xAxis.setAxisMinimum(2016f);
////        xAxis.setDrawLimitLinesBehindData(true);
////
////        LimitLine ll1 = new LimitLine(16f, "Maximum Limit");
////        ll1.setLineWidth(4f);
////        ll1.enableDashedLine(10f, 10f, 0f);
////        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
////        ll1.setTextSize(10f);
////
////        LimitLine ll2 = new LimitLine(12f, "Minimum Limit");
////        ll2.setLineWidth(4f);
////        ll2.enableDashedLine(10f, 10f, 0f);
////        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
////        ll2.setTextSize(10f);
////
////        YAxis leftAxis = mChart.getAxisLeft();
////        leftAxis.removeAllLimitLines();
////        leftAxis.addLimitLine(ll1);
////        leftAxis.addLimitLine(ll2);
////        leftAxis.setAxisMaximum(20f);
////        leftAxis.setAxisMinimum(0f);
////        leftAxis.enableGridDashedLine(10f, 10f, 0f);
////        leftAxis.setDrawZeroLine(false);
////        leftAxis.setDrawLimitLinesBehindData(false);
////
////        mChart.getAxisRight().setEnabled(false);
//        setData();
//    }

//    private void setData() {
//
//        ArrayList<Entry> values = new ArrayList<>();
////        values.add(new Entry(1, 50));
////        values.add(new Entry(2, 100));
////        values.add(new Entry(3, 80));
////        values.add(new Entry(4, 120));
////        values.add(new Entry(5, 110));
////        values.add(new Entry(7, 150));
////        values.add(new Entry(8, 250));
////        values.add(new Entry(9, 190));
//        if(connectionDetector.isInternetAvailble()) {
//            CollectionReference collectionRef = clouddb.collection("users").document(currentUser.getUid()).collection("reports");
//            collectionRef.get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    int i = 0;
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        String reportDate = document.getString("reportDate");
//                        String[] dateTokens = reportDate.split("/");
//
//                        Map mapHaemo = (Map) document.get("haemogramReport");
//                        Map haemoValue = (Map) mapHaemo.get("values");
//                        if(!haemoValue.get("haemoglobin").toString().equals("null")) {
//                            values.add(new Entry(i++, Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin")))));
//                            Log.d(TAG,"year: " + dateTokens[2] +", hb: "+ Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin"))));
//                        }
//                    }
//                }
//            });
//        }else {
//            Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
//        }
//
//
//
//        LineDataSet set1;
//        if (mChart.getData() != null &&
//                mChart.getData().getDataSetCount() > 0) {
//            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
//            set1.setValues(values);
//            mChart.getData().notifyDataChanged();
//            mChart.notifyDataSetChanged();
//        } else {
//            set1 = new LineDataSet(values, "Sample Data");
//            set1.setDrawIcons(false);
//            set1.enableDashedLine(10f, 5f, 0f);
//            set1.enableDashedHighlightLine(10f, 5f, 0f);
//            set1.setColor(Color.DKGRAY);
//            set1.setCircleColor(Color.DKGRAY);
//            set1.setLineWidth(1f);
//            set1.setCircleRadius(3f);
//            set1.setDrawCircleHole(false);
//            set1.setValueTextSize(9f);
//            set1.setDrawFilled(true);
//            set1.setFormLineWidth(1f);
//            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
//            set1.setFormSize(15.f);
//
//            if (Utils.getSDKInt() >= 18) {
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
//                set1.setFillDrawable(drawable);
//            } else {
//                set1.setFillColor(Color.DKGRAY);
//            }
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//            dataSets.add(set1);
//            LineData data = new LineData(dataSets);
//            mChart.setData(data);
//        }
//    }

}