package com.digitalpathology.digi_report.common;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.digitalpathology.digi_report.R;
import com.digitalpathology.digi_report.utils.ConnectionDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class EditReportActivity extends AppCompatActivity {

    private  LinearLayout linlaHeaderProgress;
    private TableLayout tableLayout;
    private ConnectionDetector connectionDetector;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();

    private final String TAG = EditReportActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);
//        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        setProgressBarIndeterminateVisibility(true);

        Intent i = getIntent();
        int id = i.getIntExtra("ID", -1);

        connectionDetector = new ConnectionDetector(this);

        //hooks
        tableLayout = findViewById(R.id.tablelayout_edit_report);
        // CAST THE LINEARLAYOUT HOLDING THE MAIN PROGRESS (SPINNER)
        linlaHeaderProgress = findViewById(R.id.linlaHeaderProgress);

        // SHOW THE SPINNER WHILE LOADING FEEDS
        linlaHeaderProgress.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(connectionDetector.isInternetAvailble()) {
            //read user data from firestore
            DocumentReference reportRef = clouddb.collection("users").document(currentUser.getUid())
                    .collection("reports").document(String.valueOf(id));
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
                        tableLayout.addView(createCommonHeaderRow(createCommonHeaderTextView(new String[]{"Report Information"}, 1)));
                        tableLayout.addView(createRowWE(createTextView("Report Name"), createEditText(rn)));
                        tableLayout.addView(createRowWE(createTextView("Case Number"), createEditText(document.getString("casenumber"))));
                        tableLayout.addView(createRowWE(createTextView("Report Date"), createEditText(document.getString("reportDate"))));
                        tableLayout.addView(createRowWE(createTextView("Patient Name"), createEditText(document.getString("patientName"))));
                        tableLayout.addView(createRowWE(createTextView("Sex"), createEditText(document.getString("sex"))));
                        tableLayout.addView(createRowWE(createTextView("Age"), createEditText(document.getString("age"))));
                        tableLayout.addView(createRowWE(createTextView("Referred By"), createEditText(document.getString("refferedBy"))));
                        tableLayout.addView(createRowWE(createTextView("Pathologist 1"), createEditText(document.getString("pathologist1Name"))));
                        tableLayout.addView(createRowWE(createTextView("Pathologist 2"), createEditText(document.getString("pathologist2Name"))));
                        tableLayout.addView(createCommonHeaderRow(createCommonHeaderTextView(new String[]{"Observation", "Value", "Units"}, 3)));
//
//
                        Map mapHaemo = (Map) document.get("haemogramReport");

                        Map haemoValue = (Map) mapHaemo.get("values");
                        Map haemoUnits = (Map) mapHaemo.get("units");

                        for(Object s: haemoValue.keySet()){
                            Log.d(TAG, "onCreate: "+ s.toString() + " adding");
//                            if(!haemoValue.get(s).toString().equals("null"))
//                                tableLayout.addView(createRow(createTextView(new String[]{s.toString().substring(0,1).toUpperCase() + s.toString().substring(1), (String) haemoValue.get(s), (String) haemoUnits.get(s)}, 3)));
//                            else
////                                tableLayout.addView(createHeaderRow(createHeaderTextView(new String[]{s.toString().substring(0,1).toUpperCase() + s.toString().substring(1)}, 1)));
//                            Log.d(TAG, s.toString().substring(0,1).toUpperCase() + s.toString().substring(1) + ": " + haemoValue.get(s).toString() + ", " + haemoUnits.get(s).toString());
//                            Log.d(TAG, "onCreate: "+ s.toString() + " added");
                        }

                        tableLayout.setVisibility(View.VISIBLE);
                        linlaHeaderProgress.setVisibility(View.GONE);

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


    }

    private TextView[] createCommonHeaderTextView(String text[], int total) {
        TextView[] textView = new TextView[total];
        for(int i=0; i<total; i++) {
            textView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_normal_report_info_cell, null);
            textView[i].setTextColor(getResources().getColor(R.color.white));
            textView[i].setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            textView[i].setGravity(Gravity.CENTER_HORIZONTAL);
            textView[i].setText(text[i]);
            if(i==0){
                textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            } else if(i==1){
                textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f));
            } else if(i==2){
                textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
            }
            Log.d(TAG, "createtv: " + text[i]);
            textView[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView[i].setPadding(10, 10, 10, 10);
        }
        if(total == 1){
            textView[0].setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f));
        }
        return textView;
    }



    private TextView[] createTextViews(String text[], int total) {
        TextView[] textView = new TextView[total];
        for(int i=0; i<total; i++) {
            textView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_normal_report_info_cell, null);
            textView[i].setTextColor(getResources().getColor(R.color.black));
            textView[i].setBackgroundColor(getResources().getColor(R.color.white));
            textView[i].setText(text[i]);
            if(total == 3)
                if(i==0)
                    textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
                else if(i==1)
                    textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.4f));
                else if(i==2)
                    textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.3f));
                else if(total == 2)
                    textView[i].setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            textView[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView[i].setPadding(10, 10, 10, 10);
        }
        return textView;

    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
//        for(int i=0; i<total; i++) {
            textView = (TextView) getLayoutInflater().inflate(R.layout.template_normal_report_info_cell, null);
            textView.setLayoutParams(new TableLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0.5f));
            textView.setTextColor(getResources().getColor(R.color.black));
            textView.setBackgroundColor(getResources().getColor(R.color.white));
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            textView.setPadding(10, 10, 10, 10);
//        }
        return textView;

    }

    private EditText createEditText(String text){
        EditText editText = new EditText(this);
        editText = (EditText) getLayoutInflater().inflate(R.layout.template_normal_info_edittext_cell, null);
        editText.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f));
        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setFocusable(false);
        editText.setText(text, TextView.BufferType.EDITABLE);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        editText.setBackgroundColor(getResources().getColor(R.color.white));
        editText.setPadding(10, 10, 10, 10);
        return editText;
    }

    private TextView[] createHeaderTextView(String text[], int total) {
        TextView[] textView = new TextView[total];
        for(int i=0; i<total; i++) {
            textView[i] = (TextView) getLayoutInflater().inflate(R.layout.template_normal_report_info_cell, null);
            textView[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView[i].setTextColor(getResources().getColor(R.color.black));
            textView[i].setBackgroundColor(getResources().getColor(R.color.startup_background_color));
            textView[i].setText(text[i]);
            textView[i].setGravity(Gravity.CENTER_HORIZONTAL);
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
        tr.setGravity(Gravity.CENTER_HORIZONTAL);

        for(int i=0; i<tv.length; i++) {
            TableRow.LayoutParams columnParams = new TableRow.LayoutParams();
            // wrap-up content of the row
            columnParams.height = TableRow.LayoutParams.WRAP_CONTENT;
            columnParams.width = TableRow.LayoutParams.WRAP_CONTENT;
            // set gravity to center of the column
            columnParams.gravity = Gravity.CENTER_HORIZONTAL;
            tr.addView(tv[i],columnParams);
        }
        return tr;
    }

    private TableRow createRowWE(TextView tv, EditText et){
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(getResources().getColor(R.color.white));
        tr.setGravity(Gravity.CENTER_HORIZONTAL);

//        for(int i=0; i<tv.length; i++) {
            TableRow.LayoutParams columnParams = new TableRow.LayoutParams();
            // wrap-up content of the row
            columnParams.height = TableRow.LayoutParams.WRAP_CONTENT;
            columnParams.width = TableRow.LayoutParams.WRAP_CONTENT;
            // set gravity to center of the column
            columnParams.gravity = Gravity.CENTER_HORIZONTAL;
            tr.addView(tv,columnParams);
            tr.addView(et, columnParams);
//        }
        return tr;
    }

    private TableRow createCommonHeaderRow(TextView[] tv){
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tr.setGravity(Gravity.CENTER_HORIZONTAL);

        for(int i=0; i<tv.length; i++) {
            TableRow.LayoutParams columnParams = new TableRow.LayoutParams();
            // wrap-up content of the row
            columnParams.height = TableRow.LayoutParams.WRAP_CONTENT;
            columnParams.width = TableRow.LayoutParams.MATCH_PARENT;
            // set gravity to center of the column
            columnParams.gravity = Gravity.CENTER_HORIZONTAL;
            tr.addView(tv[i],columnParams);
        }
        return tr;
    }
}