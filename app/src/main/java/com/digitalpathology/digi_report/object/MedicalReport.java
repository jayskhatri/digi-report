package com.digitalpathology.digi_report.object;

import com.digitalpathology.digi_report.object.Reports.*;

public class MedicalReport {

    String patientName;
    String refferedBy;
    String reportDate;
    String age;
    String sex;
    String address;
    int refno;
    int casenumber;
    String uploadDate;

    HaemogramReport haemogramReport;
    BloodSugrarLevel bloodSugrarLevel;
    RenalFunctionTests renalFunctionTests;
    LiverFunctionTest liverFunctionTest;

    String pathologistName;

}
