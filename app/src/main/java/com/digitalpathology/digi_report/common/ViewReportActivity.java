package com.digitalpathology.digi_report.common;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

    private TextView valueHB, valueRBC, valueWBC, valuePlatelets, valueBloodSugar, valueBloodUrea, valueUrineSugar;
    private TextView unitHB, unitRBC, unitWBC, unitPlatelets, unitBloodSugar, unitBloodUrea, unitUrineSugar;
    private TextView valueBloodUreaNitrogen, valueSerumCreatinine, valueSerumUricAcid, valueBiliRubinTotal, valueBiliRubinDirect, valueBiliRubinIndirect;
    private TextView unitBloodUreaNitrogen, unitSerumCreatinine, unitSerumUricAcid, unitBiliRubinTotal, unitBiliRubinDirect, unitBiliRubinIndirect;
    private TextView valueSGPT, valueAlkPhosphatase, valueSGOT, valueSerumProtiTotal, valueSerumProtiAlbumin, valueSerumProtiGlobulins, valueSerumProtiAGRatio;
    private TextView unitSGPT, unitAlkPhosphatase, unitSGOT, unitSerumProtiTotal, unitSerumProtiAlbumin, unitSerumProtiGlobulins, unitSerumProtiAGRatio;
    private MedicalReport medicalReport;
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

        valueHB = findViewById(R.id.value_hb);
        unitHB = findViewById(R.id.unit_hb);

        valueRBC = findViewById(R.id.value_rbc);
        unitRBC = findViewById(R.id.unit_rbc);

        valueWBC = findViewById(R.id.value_wbc);
        unitWBC = findViewById(R.id.unit_wbc);

        valuePlatelets = findViewById(R.id.value_platelet);
        unitPlatelets = findViewById(R.id.unit_platelet);

        valueBloodSugar = findViewById(R.id.value_bloodsugar);
        unitBloodSugar = findViewById(R.id.unit_blood_sugar);

        valueBloodUrea = findViewById(R.id.value_blood_urea);
        unitBloodUrea = findViewById(R.id.unit_blood_urea);

        valueUrineSugar = findViewById(R.id.value_urine_sugar);
        unitUrineSugar = findViewById(R.id.unit_urine_sugar);

        valueBloodUreaNitrogen = findViewById(R.id.value_blood_urea_n);
        unitBloodUreaNitrogen = findViewById(R.id.unit_blood_urea_n);

        valueSerumCreatinine  = findViewById(R.id.value_serum_creatinine);
        unitSerumCreatinine = findViewById(R.id.unit_serum_creatinine);

        valueSerumUricAcid = findViewById(R.id.value_serum_uric_acid);
        unitSerumUricAcid = findViewById(R.id.unit_serum_uric_acid);

        valueBiliRubinTotal = findViewById(R.id.value_s_bilirubin_total);
        unitBiliRubinTotal = findViewById(R.id.unit_s_bilirubin_total);

        valueBiliRubinDirect = findViewById(R.id.value_s_bilirubin_direct);
        unitBiliRubinDirect = findViewById(R.id.unit_s_bilirubin_direct);

        valueBiliRubinIndirect = findViewById(R.id.value_s_bilirubin_indirect);
        unitBiliRubinIndirect = findViewById(R.id.unit_s_bilirubin_indirect);

        valueSGPT = findViewById(R.id.value_sgpt);
        unitSGPT = findViewById(R.id.unit_sgpt);

        valueAlkPhosphatase = findViewById(R.id.value_alkaline_phosphatase);
        unitAlkPhosphatase = findViewById(R.id.unit_alkaline_phosphatase);

        valueSGOT = findViewById(R.id.value_sgot);
        unitSGOT = findViewById(R.id.unit_sgot);

        valueSerumProtiTotal = findViewById(R.id.value_serum_proteins_total);
        unitSerumProtiTotal = findViewById(R.id.unit_serum_proteins_total);

        valueSerumProtiAlbumin = findViewById(R.id.value_serum_proteins_albumin);
        unitSerumProtiAlbumin = findViewById(R.id.unit_serum_albumin);

        valueSerumProtiGlobulins = findViewById(R.id.value_serum_proteins_globulin);
        unitSerumProtiGlobulins = findViewById(R.id.unit_serum_proteins_globulin);

        valueSerumProtiAGRatio = findViewById(R.id.value_serum_proteins_ag_ratio);
        unitSerumProtiAGRatio = findViewById(R.id.unit_serum_proteins_ag_ratio);

        valueBloodUrea = findViewById(R.id.value_blood_urea);
        unitBloodUrea = findViewById(R.id.unit_blood_urea);

        valueBloodUreaNitrogen = findViewById(R.id.value_blood_urea_n);
        unitBloodUreaNitrogen = findViewById(R.id.unit_blood_urea_n);

        valueSerumCreatinine = findViewById(R.id.value_serum_creatinine);
        unitSerumCreatinine = findViewById(R.id.unit_serum_creatinine);

        valueSerumUricAcid = findViewById(R.id.value_serum_uric_acid);
        unitSerumUricAcid = findViewById(R.id.unit_serum_uric_acid);
        
        
        connectionDetector = new ConnectionDetector(this);

        Log.d(TAG, "id: "+ id);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(connectionDetector.isInternetAvailble()) {
            progressBar.setVisibility(View.VISIBLE);

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
                        Map mapHaemo = (Map) document.get("haemogramReport");
                        Map mapRenal = (Map) document.get("renalFunctionTests");
                        Map mapBloodSugar = (Map) document.get("bloodSugrarLevel");
                        Map mapLiverFunc = (Map) document.get("liverFunctionTest");

                        //float SBilirubinTotal, float SBilirubinDirect, float SBilirubinIndirect, float SGPT,
                        //                             float SAlkalinePhosphatse, float SGOT, float serumPotiTotal, float serumPotiAlbumin,
                        //                             float serumPotiGlobulins, float serumPotiAGRatio, String SBilirubinUnit, String serumProtiUnit,
                        //                             String sgptUnit, String sgotUnit, String SAlkalinePhosphatseUnit
                        LiverFunctionTest liverFunctionTest = new LiverFunctionTest(Float.parseFloat(String.valueOf(mapLiverFunc.get("sbilirubinTotal"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("sbilirubinDirect"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("sbilirubinIndirect"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("sgpt"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("salkalinePhosphatse"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("sgot"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("serumPotiTotal"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("serumPotiAlbumin"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("serumPotiGlobulins"))),
                                Float.parseFloat(String.valueOf(mapLiverFunc.get("serumPotiAGRatio"))),
                                String.valueOf(mapLiverFunc.get("sbilirubinUnit")),
                                String.valueOf(mapLiverFunc.get("serumProtiUnit")),
                                String.valueOf(mapLiverFunc.get("sgptUnit")),
                                String.valueOf(mapLiverFunc.get("sgotUnit")),
                                String.valueOf(mapLiverFunc.get("sAlkalinePhosphatseUnit")));

                        //float bloodSugarResult, String bloodSugarUnit, float urineSugarResult, String urineSugarUnit
                        BloodSugrarLevel bloodSugrarLevel = new BloodSugrarLevel(Float.parseFloat(String.valueOf(mapBloodSugar.get("bloodSugarResult"))),
                                String.valueOf(mapBloodSugar.get("bloodSugarUnit")), Float.parseFloat(String.valueOf(mapBloodSugar.get("urineSugarResult"))),
                                String.valueOf(mapBloodSugar.get("urineSugarUnit")));

                        //float hb, float rbc, float wbc, float platelets, String hbUnit, String rbcUnit, String wbcUnit, String plateletsUnit
                        HaemogramReport haemogramReport = new HaemogramReport(Float.parseFloat(String.valueOf(mapHaemo.get("hb"))), Float.parseFloat(String.valueOf(mapHaemo.get("rbc"))),
                                Float.parseFloat(String.valueOf(mapHaemo.get("wbc"))), Float.parseFloat(String.valueOf(mapHaemo.get("platelets"))), String.valueOf(mapHaemo.get("hbUnit")),
                                String.valueOf(mapHaemo.get("rbcUnit")), String.valueOf(mapHaemo.get("wbcUnit")), String.valueOf(mapHaemo.get("plateletUnit")));

                        //float bloodUrea, float bloodUreaNitrogen, float serumCreatinine, float serumUricAcid, String commonUnit
                        RenalFunctionTests renalFunctionTests = new RenalFunctionTests(Float.parseFloat(String.valueOf(mapRenal.get("bloodUrea"))),
                                Float.parseFloat(String.valueOf(mapRenal.get("bloodUreaNitrogen"))), Float.parseFloat(String.valueOf(mapRenal.get("serumCreatinine"))),
                                Float.parseFloat(String.valueOf(mapRenal.get("serumUricAcid"))), String.valueOf(mapRenal.get("commonUnit")));

                        medicalReport = document.toObject(MedicalReport.class);
                        medicalReport.setHaemogramReport(haemogramReport);
                        medicalReport.setLiverFunctionTest(liverFunctionTest);
                        medicalReport.setBloodSugrarLevel(bloodSugrarLevel);
                        medicalReport.setRenalFunctionTests(renalFunctionTests);
                        Log.d(TAG, "report name: " + medicalReport.getReportName());

                        setupData(medicalReport);

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

    private void setupData(MedicalReport medicalReport){
        valueHB.setText("" + medicalReport.getHaemogramReport().getHb());
        unitHB.setText("" + medicalReport.getHaemogramReport().getHbUnit());

        valueRBC.setText("" + medicalReport.getHaemogramReport().getRbc());
        unitRBC.setText(""+ medicalReport.getHaemogramReport().getRbcUnit());

        valueWBC.setText("" + medicalReport.getHaemogramReport().getWbc());
        unitWBC.setText("" + medicalReport.getHaemogramReport().getWbcUnit());
        valuePlatelets.setText("" + medicalReport.getHaemogramReport().getPlatelets());

        valueBloodSugar.setText("" + medicalReport.getBloodSugrarLevel().getBloodSugarResult());
        valueUrineSugar.setText("" + medicalReport.getBloodSugrarLevel().getUrineSugarResult());

        valueBiliRubinTotal.setText("" + medicalReport.getLiverFunctionTest().getSBilirubinTotal());
        valueBiliRubinDirect.setText("" + medicalReport.getLiverFunctionTest().getSBilirubinDirect());
        valueBiliRubinIndirect.setText("" + medicalReport.getLiverFunctionTest().getSBilirubinIndirect());
        valueSGPT.setText("" + medicalReport.getLiverFunctionTest().getSGPT());
        valueSGOT.setText("" + medicalReport.getLiverFunctionTest().getSGOT());
        valueAlkPhosphatase.setText("" + medicalReport.getLiverFunctionTest().getSAlkalinePhosphatse());
        valueSerumProtiTotal.setText("" + medicalReport.getLiverFunctionTest().getSerumPotiTotal());
        valueSerumProtiAlbumin.setText("" + medicalReport.getLiverFunctionTest().getSerumPotiAlbumin());
        valueSerumProtiGlobulins.setText("" + medicalReport.getLiverFunctionTest().getSerumPotiGlobulins());
        valueSerumProtiAGRatio.setText("" + medicalReport.getLiverFunctionTest().getSerumPotiAGRatio());

        valueBloodUrea.setText("" + medicalReport.getRenalFunctionTests().getBloodUrea());
        valueBloodUreaNitrogen.setText("" + medicalReport.getRenalFunctionTests().getBloodUreaNitrogen());
        valueSerumCreatinine.setText("" + medicalReport.getRenalFunctionTests().getSerumCreatinine());
        valueSerumUricAcid.setText("" + medicalReport.getRenalFunctionTests().getSerumUricAcid());
    }
}