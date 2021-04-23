package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private final String TAG = "ViewReportActivity";
    private Context c;

    private TableLayout tableLayout;
    private Button normalRange;
    private ConnectionDetector connectionDetector;
    private FirebaseFirestore clouddb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        Intent i = getIntent();
        int id = i.getIntExtra("id", -1);

        //hooks
        tableLayout = findViewById(R.id.tablelayout);
        c = this;
        normalRange = findViewById(R.id.btn_normal_range);
        connectionDetector = new ConnectionDetector(this);

        Log.d(TAG, "id: "+ id);
        normalRange.setOnClickListener(v -> createDialog(c).show());

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
                        tableLayout.addView(createRow(createTextView(new String[]{"Report Name", rn}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Case Number", document.getString("casenumber")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Report Date", document.getString("reportDate")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Patient Name", document.getString("patientName")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Sex", document.getString("sex")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Age", document.getString("age")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Referred By", document.getString("refferedBy")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Pathologist 1", document.getString("pathologist1Name")}, 2)));
                        tableLayout.addView(createRow(createTextView(new String[]{"Pathologist 2", document.getString("pathologist2Name")}, 2)));
                        tableLayout.addView(createCommonHeaderRow(createCommonHeaderTextView(new String[]{"Observation", "Value", "Units"}, 3)));


                        Map mapHaemo = (Map) document.get("haemogramReport");

                        Map haemoValue = (Map) mapHaemo.get("values");
                        Map haemoUnits = (Map) mapHaemo.get("units");

                        for(Object s: haemoValue.keySet()){
                            Log.d(TAG, "onCreate: "+ s.toString() + " adding");
                            if(!haemoValue.get(s).toString().equals("null"))
                                tableLayout.addView(createRow(createTextView(new String[]{s.toString().substring(0,1).toUpperCase() + s.toString().substring(1), (String) haemoValue.get(s), (String) haemoUnits.get(s)}, 3)));
//                            else
//                                tableLayout.addView(createHeaderRow(createHeaderTextView(new String[]{s.toString().substring(0,1).toUpperCase() + s.toString().substring(1)}, 1)));
                            Log.d(TAG, s.toString().substring(0,1).toUpperCase() + s.toString().substring(1) + ": " + haemoValue.get(s).toString() + ", " + haemoUnits.get(s).toString());
                            Log.d(TAG, "onCreate: "+ s.toString() + " added");
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

    private TextView[] createTextView(String text[], int total) {
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

    private TableRow createHeaderRow(TextView[] tv){
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(getResources().getColor(R.color.startup_background_color));
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

    private AlertDialog createDialog(Context context){
        Log.d(TAG, "createDialog called");
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(R.layout.popup_normal_range,null);
        final AlertDialog processDialog = new AlertDialog.Builder(context).create();
        processDialog.setView(dialogView);
        return processDialog;
    }
}