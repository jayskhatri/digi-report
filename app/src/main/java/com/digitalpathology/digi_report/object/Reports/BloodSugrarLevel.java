package com.digitalpathology.digi_report.object.Reports;

public class BloodSugrarLevel {

    float bloodSugarResult;
    String bloodSugarUnit;

    float urineSugarResult;
    String urineSugarUnit;

    public BloodSugrarLevel(float bloodSugarResult, String bloodSugarUnit, float urineSugarResult, String urineSugarUnit) {
        this.bloodSugarResult = bloodSugarResult;
        this.bloodSugarUnit = bloodSugarUnit;
        this.urineSugarResult = urineSugarResult;
        this.urineSugarUnit = urineSugarUnit;
    }

    public float getBloodSugarResult() {
        return bloodSugarResult;
    }

    public void setBloodSugarResult(float bloodSugarResult) {
        this.bloodSugarResult = bloodSugarResult;
    }

    public String getBloodSugarUnit() {
        return bloodSugarUnit;
    }

    public void setBloodSugarUnit(String bloodSugarUnit) {
        this.bloodSugarUnit = bloodSugarUnit;
    }

    public float getUrineSugarResult() {
        return urineSugarResult;
    }

    public void setUrineSugarResult(float urineSugarResult) {
        this.urineSugarResult = urineSugarResult;
    }

    public String getUrineSugarUnit() {
        return urineSugarUnit;
    }

    public void setUrineSugarUnit(String urineSugarUnit) {
        this.urineSugarUnit = urineSugarUnit;
    }
}
