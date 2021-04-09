package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.object.MedicalReport;
import com.digitalpathology.digi_report.object.Reports.BloodSugrarLevel;
import com.digitalpathology.digi_report.object.Reports.HaemogramReport;
import com.digitalpathology.digi_report.object.Reports.LiverFunctionTest;
import com.digitalpathology.digi_report.object.Reports.RenalFunctionTests;
import com.digitalpathology.digi_report.object.User;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Map;

public class ViewReportActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private final String TAG = "ViewReportActivity";

    private TextView reportName, reportDate, patientName, patientAge, patientSex, pathologist, refferedBy, caseNumber;
    private TextView valueHB, valueRBC, valueWBC, valuePlatelets, valuePolymorphs, valueLympho, valueEosino, valueMono, valueBaso,valueMCV, valueMCH, valueMCHC, valueRDW;
    private TextView unitHB, unitRBC, unitWBC, unitPlatelets, unitPoly, unitLympho, unitEosino, unitMono, unitBaso, unitMCV, unitMCH, unitMCHC, unitRDW;
    private TextView valueBloodSugar, valueBloodUrea, valueUrineSugar, unitBloodSugar, unitBloodUrea, unitUrineSugar;
    private TextView valueBloodUreaNitrogen, valueSerumCreatinine, valueSerumUricAcid, valueBiliRubinTotal, valueBiliRubinDirect, valueBiliRubinIndirect;
    private TextView unitBloodUreaNitrogen, unitSerumCreatinine, unitSerumUricAcid, unitBiliRubinTotal, unitBiliRubinDirect, unitBiliRubinIndirect;
    private TextView valueSGPT, valueAlkPhosphatase, valueSGOT, valueSerumProtiTotal, valueSerumProtiAlbumin, valueSerumProtiGlobulins, valueSerumProtiAGRatio;
    private TextView unitSGPT, unitAlkPhosphatase, unitSGOT, unitSerumProtiTotal, unitSerumProtiAlbumin, unitSerumProtiGlobulins, unitSerumProtiAGRatio;
    private MedicalReport medicalReport;
    private TableLayout tableLayout;
    private ConnectionDetector connectionDetector;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        Intent i = getIntent();
        int id = i.getIntExtra("id", -1);

        //hooks
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        tableLayout = findViewById(R.id.tablelayout);
        connectionDetector = new ConnectionDetector(this);

        Log.d(TAG, "id: "+ id);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(connectionDetector.isInternetAvailble()) {
            //read user data from firestore
            DocumentReference reportRef = clouddb.collection("users").document(currentUser.getUid()).collection("reports").document(String.valueOf(id));
            reportRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        /*int id, String reportName, String patientName, String refferedBy, String reportDate, int age, String sex,
                         String address, int refno, int casenumber, String uploadDate, HaemogramReport haemogramReport,
                         BloodSugrarLevel bloodSugrarLevel, RenalFunctionTests renalFunctionTests, LiverFunctionTest liverFunctionTest,
                         String conclusion, String advise, String bloodGroup, String pathologistName*/

                        String rn = document.getString("reportName");
                        Log.d(TAG, "reportdate: " + document.getString("reportDate"));
                        tableLayout.addView(createRow(createCommonHeaderTextView(new String[]{"Report Information"}, 1)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Report Name", rn}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Case Number", String.valueOf(document.getDouble("casenumber"))}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Report Date", document.getString("reportDate")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Patient Name", document.getString("patientName")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Sex", document.getString("sex")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"age", document.getString("age")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Referred By", document.getString("refferedBy")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Pathologist 1", document.getString("pathologist1Name")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Pathologist 2", document.getString("pathologist2Name")}, 2)));
                        tableLayout.addView(createRow(createCommonHeaderTextView(new String[]{"Observation", "Value", "Units"}, 3)));


                        Map mapHaemo = (Map) document.get("haemogramReport");

                        Map haemoValue = (Map) mapHaemo.get("values");
                        Map haemoUnits = (Map) mapHaemo.get("units");

                        for(Object s: haemoValue.keySet()){
                            if(haemoValue.get(s).toString() != "null")
                            tableLayout.addView(createRow(createTextView(new String[]{s.toString(), (String) haemoValue.get(s), (String) haemoUnits.get(s)}, 3)));
                            Log.d(TAG, s.toString() + ": " + haemoValue.get(s).toString() + ", " + haemoUnits.get(s).toString());
                        }

                        tableLayout.setVisibility(View.VISIBLE);

                    } else {
                        Log.d(TAG, "report does not exist");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });

        }else {
            Toast.makeText(this, "Internet unavailable", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.GONE);

    }


    private TextView[] createCommonHeaderTextView(String text[], int total) {
        TextView[] textView = new TextView[total];
        for(int i=0; i<total; i++) {
            textView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_normal_report_info_cell, null);
            textView[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView[i].setTextColor(getResources().getColor(R.color.white));
            textView[i].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            textView[i].setText(text[i]);
            Log.d(TAG, "createtv: " + text[i]);
            textView[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView[i].setPadding(10, 10, 10, 10);
        }
        return textView;
    }

    private TextView[] createTextView(String text[], int total) {
        TextView[] textView = new TextView[total];
        for(int i=0; i<total; i++) {
            textView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_normal_report_info_cell, null);
            textView[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView[i].setTextColor(getResources().getColor(R.color.black));
            textView[i].setBackgroundColor(getResources().getColor(R.color.white));
            textView[i].setText(text[i]);
            Log.d(TAG, "createtv: " + text[i]);
            textView[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView[i].setPadding(10, 10, 10, 10);
        }
        return textView;

    }

    private TableRow createRow(TextView[] tv){
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(getResources().getColor(R.color.white));

        for(int i=0; i<tv.length; i++) {
            TableRow.LayoutParams columnParams = new TableRow.LayoutParams();
            // wrap-up content of the row
            columnParams.height = TableRow.LayoutParams.WRAP_CONTENT;
            columnParams.width = TableRow.LayoutParams.WRAP_CONTENT;
            // set gravity to center of the column
            columnParams.gravity = Gravity.CENTER;
            tr.addView(tv[i],columnParams);
        }
        return tr;
    }
}