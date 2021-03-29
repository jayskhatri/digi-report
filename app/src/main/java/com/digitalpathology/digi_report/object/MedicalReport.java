package com.digitalpathology.digi_report.object;

import com.digitalpathology.digi_report.object.Reports.*;

public class MedicalReport {
    String reportName;
    String patientName;
    String refferedBy;
    String reportDate;
    int age;
    String sex;
    String address;
    int refno;
    int casenumber;
    String uploadDate;

    HaemogramReport haemogramReport;
    BloodSugrarLevel bloodSugrarLevel;
    RenalFunctionTests renalFunctionTests;
    LiverFunctionTest liverFunctionTest;

    String conclusion;
    String advise;
    String bloodGroup;
    String pathologistName;

    public MedicalReport() {    }

    public MedicalReport(String reportName, String patientName, String refferedBy, String reportDate, int age, String sex,
                         String address, int refno, int casenumber, String uploadDate, HaemogramReport haemogramReport,
                         BloodSugrarLevel bloodSugrarLevel, RenalFunctionTests renalFunctionTests, LiverFunctionTest liverFunctionTest,
                         String conclusion, String advise, String bloodGroup, String pathologistName) {
        this.reportName = reportName;
        this.patientName = patientName;
        this.refferedBy = refferedBy;
        this.reportDate = reportDate;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.refno = refno;
        this.casenumber = casenumber;
        this.uploadDate = uploadDate;
        this.haemogramReport = haemogramReport;
        this.bloodSugrarLevel = bloodSugrarLevel;
        this.renalFunctionTests = renalFunctionTests;
        this.liverFunctionTest = liverFunctionTest;
        this.conclusion = conclusion;
        this.advise = advise;
        this.bloodGroup = bloodGroup;
        this.pathologistName = pathologistName;
    }

    public String getReportName() { return reportName; }

    public void setReportName(String reportName) { this.reportName = reportName; }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getRefferedBy() {
        return refferedBy;
    }

    public void setRefferedBy(String refferedBy) {
        this.refferedBy = refferedBy;
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRefno() {
        return refno;
    }

    public void setRefno(int refno) {
        this.refno = refno;
    }

    public int getCasenumber() {
        return casenumber;
    }

    public void setCasenumber(int casenumber) {
        this.casenumber = casenumber;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public HaemogramReport getHaemogramReport() {
        return haemogramReport;
    }

    public void setHaemogramReport(HaemogramReport haemogramReport) {
        this.haemogramReport = haemogramReport;
    }

    public BloodSugrarLevel getBloodSugrarLevel() {
        return bloodSugrarLevel;
    }

    public void setBloodSugrarLevel(BloodSugrarLevel bloodSugrarLevel) {
        this.bloodSugrarLevel = bloodSugrarLevel;
    }

    public RenalFunctionTests getRenalFunctionTests() {
        return renalFunctionTests;
    }

    public void setRenalFunctionTests(RenalFunctionTests renalFunctionTests) {
        this.renalFunctionTests = renalFunctionTests;
    }

    public LiverFunctionTest getLiverFunctionTest() {
        return liverFunctionTest;
    }

    public void setLiverFunctionTest(LiverFunctionTest liverFunctionTest) {
        this.liverFunctionTest = liverFunctionTest;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getAdvise() {
        return advise;
    }

    public void setAdvise(String advise) {
        this.advise = advise;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getPathologistName() {
        return pathologistName;
    }

    public void setPathologistName(String pathologistName) {
        this.pathologistName = pathologistName;
    }

}
