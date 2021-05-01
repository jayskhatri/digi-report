package com.digitalpathology.digi_report.common;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.digitalpathology.digi_report.utils.HBMarkerView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class HBVisualisationActivity extends AppCompatActivity {

    private LineChart mChart;
    private ConnectionDetector connectionDetector;
    private Button shareBtn, saveBtn;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//    private long reference_timestamp = 1451660400;
    private final static String TAG = HBVisualisationActivity.class.getSimpleName();
    private final int REQUEST_PERMISSION = 774;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hb_visualisation);

        //hooks
        mChart = findViewById(R.id.lineChart_view);
        shareBtn = findViewById(R.id.btn_back);
        saveBtn = findViewById(R.id.btn_save_graph);
        connectionDetector = new ConnectionDetector(this);

        //save chart
        saveBtn.setOnClickListener(v->{
            saveGraph(mChart);
        });

        //back from the chart
        shareBtn.setOnClickListener(v-> shareVisualization(mChart));

        //chart characteristics
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        mChart.getDescription().setText("This graph shows historic Haemoglobin data");
        //setting marker
        HBMarkerView mv = new HBMarkerView(getApplicationContext(), R.layout.custom_marker_view);
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
                return sdf.format(new Date(convertedTS));
            }
        });
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(18f, "Maximum Limit");
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

        //reading hb data from the firestore
        if(connectionDetector.isInternetAvailble()) {
            CollectionReference collectionRef = clouddb.collection("users").document(currentUser.getUid()).collection("reports");
            collectionRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String reportDate = document.getString("reportDate");
                        try {
//                            long newTS = Objects.requireNonNull(new SimpleDateFormat("dd/MM/yyyy").parse(reportDate)).getTime() - reference_timestamp;
                            Map mapHaemo = (Map) document.get("haemogramReport");
                            Map haemoValue = (Map) mapHaemo.get("values");
                            if (!haemoValue.get("haemoglobin").toString().equals("null")) {
                                values.add(new Entry(new SimpleDateFormat("dd/MM/yyyy").parse(reportDate).getTime(), Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin")))));
//                                yYear.add(reportDate);
//                                Log.d(TAG, "setData year: " + reportDate + ", ts: "+ new SimpleDateFormat("dd/MM/yyyy").parse(reportDate).getTime() + ", hb: " + Float.parseFloat(String.valueOf(haemoValue.get("haemoglobin"))));
                            }
                        } catch (ParseException e) {
//                            Log.e(TAG, "setData: "+ e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    //sorting to x values
                    Collections.sort(values, new EntryXComparator());

                    LineDataSet set1;
                    if (mChart.getData() != null &&
                            mChart.getData().getDataSetCount() > 0) {
                        set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
//                        Log.d(TAG, "setData: size: "+ values.size());
                        set1.setValues(values);
                        mChart.getData().notifyDataChanged();
                        mChart.notifyDataSetChanged();
                    } else {
                        set1 = new LineDataSet(values, "Hemoglobin");
                        set1.setDrawIcons(false);
                        set1.enableDashedLine(10f, 5f, 0f);
                        set1.enableDashedHighlightLine(10f, 5f, 0f);
                        set1.setColor(getResources().getColor(R.color.maroon));
                        set1.setCircleColor(getResources().getColor(R.color.maroon));
                        set1.setLineWidth(1f);
                        set1.setCircleRadius(3f);
                        set1.setDrawCircleHole(false);
                        set1.setValueTextSize(9f);
                        set1.setDrawFilled(true);
                        set1.setFormLineWidth(1f);
                        set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                        set1.setFormSize(15.f);

                        if (Utils.getSDKInt() >= 18) {
                            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_maroon);
                            set1.setFillDrawable(drawable);
                        } else {
                            set1.setFillColor(getResources().getColor(R.color.maroon));
                        }
//                        Log.d(TAG, "setData: " + set1.toSimpleString());
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

    private void shareVisualization(LineChart lineChart){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        Bitmap bitmap = getBitmapFromView(lineChart);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();


        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        File photo = new File(Environment.getExternalStorageDirectory() + File.separator + "visualization" + ".png");
        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(bytes);
            fos.close();
        }
        catch (IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+ "visualization" + ".png"));
        startActivity(Intent.createChooser(share, "Share Visualization"));
    }


    private void saveGraph(LineChart lineChart){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
        }

        Bitmap bitmap = getBitmapFromView(lineChart);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        String savephotoName = "HBVisualization" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".png";
        SavePhotoTask t = new SavePhotoTask(savephotoName);
        t.execute(data);
        Toast.makeText(this, "Image saved to Downloads directory", Toast.LENGTH_SHORT).show();
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}