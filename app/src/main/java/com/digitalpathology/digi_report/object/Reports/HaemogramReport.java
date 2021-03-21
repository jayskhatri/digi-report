package com.digitalpathology.digi_report.object.Reports;

import com.digitalpathology.digi_report.object.RangeValidator;

public class HaemogramReport {

    float hb, rbc, wbc, platelets;
    String hbUnit, rbcUnit, wbcUnit, plateletsUnit;

    public HaemogramReport(float hb, float rbc, float wbc, float platelets, String hbUnit, String rbcUnit, String wbcUnit, String plateletsUnit) {
        this.hb = hb;
        this.rbc = rbc;
        this.wbc = wbc;
        this.platelets = platelets;
        this.hbUnit = hbUnit;
        this.rbcUnit = rbcUnit;
        this.wbcUnit = wbcUnit;
        this.plateletsUnit = plateletsUnit;
    }

    public float getHb() {
        return hb;
    }

    public void setHb(float hb) {
        this.hb = hb;
    }

    public float getRbc() {
        return rbc;
    }

    public void setRbc(float rbc) {
        this.rbc = rbc;
    }

    public float getWbc() {
        return wbc;
    }

    public void setWbc(float wbc) {
        this.wbc = wbc;
    }

    public float getPlatelets() {
        return platelets;
    }

    public void setPlatelets(float platelets) {
        this.platelets = platelets;
    }

//    public boolean rangeCheck(){
//        RangeValidator rangeValidator = new RangeValidator();
//        return rangeValidator.bloodCounts(hb, hbUnit, rbc, rbcUnit, wbc, wbcUnit, platelets, plateletsUnit, sex);
//    }
}
