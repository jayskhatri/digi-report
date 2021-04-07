package com.digitalpathology.digi_report.object.Reports;

import com.digitalpathology.digi_report.object.RangeValidator;

public class HaemogramReport {

    float hb, rbc, wbc, platelets, polymorphs, lymphocytes, eosinophils, monocytes, basophils, mcv, mch, mchc, rdw;
    String hbUnit, rbcUnit, wbcUnit, plateletsUnit, diffCountUnits, mcvUnit, mchUnit, mchcUnit, rdwUnit;

    public HaemogramReport() {
    }

    public HaemogramReport(float hb, float rbc, float wbc, float platelets, String hbUnit, String rbcUnit, String wbcUnit, String plateletsUnit,
                           float polymorphs, float lymphocytes, float eosinophils, float monocytes, float basophils, float mcv, float mch, float mchc,
                           float rdw, String diffCountUnits, String mcvUnit, String mchUnit, String mchcUnit, String rdwUnit) {
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

    public String getHbUnit() {
        return hbUnit;
    }

    public void setHbUnit(String hbUnit) {
        this.hbUnit = hbUnit;
    }

    public String getRbcUnit() {
        return rbcUnit;
    }

    public void setRbcUnit(String rbcUnit) {
        this.rbcUnit = rbcUnit;
    }

    public String getWbcUnit() {
        return wbcUnit;
    }

    public void setWbcUnit(String wbcUnit) {
        this.wbcUnit = wbcUnit;
    }

    public String getPlateletsUnit() {
        return plateletsUnit;
    }

    public void setPlateletsUnit(String plateletsUnit) {
        this.plateletsUnit = plateletsUnit;
    }

    public float getPolymorphs() {
        return polymorphs;
    }

    public void setPolymorphs(float polymorphs) {
        this.polymorphs = polymorphs;
    }

    public float getLymphocytes() {
        return lymphocytes;
    }

    public void setLymphocytes(float lymphocytes) {
        this.lymphocytes = lymphocytes;
    }

    public float getEosinophils() {
        return eosinophils;
    }

    public void setEosinophils(float eosinophils) {
        this.eosinophils = eosinophils;
    }

    public float getMonocytes() {
        return monocytes;
    }

    public void setMonocytes(float monocytes) {
        this.monocytes = monocytes;
    }

    public float getBasophils() {
        return basophils;
    }

    public void setBasophils(float basophils) {
        this.basophils = basophils;
    }

    public float getMcv() {
        return mcv;
    }

    public void setMcv(float mcv) {
        this.mcv = mcv;
    }

    public float getMch() {
        return mch;
    }

    public void setMch(float mch) {
        this.mch = mch;
    }

    public float getMchc() {
        return mchc;
    }

    public void setMchc(float mchc) {
        this.mchc = mchc;
    }

    public float getRdw() {
        return rdw;
    }

    public void setRdw(float rdw) {
        this.rdw = rdw;
    }

    public String getDiffCountUnits() {
        return diffCountUnits;
    }

    public void setDiffCountUnits(String diffCountUnits) {
        this.diffCountUnits = diffCountUnits;
    }

    public String getMcvUnit() {
        return mcvUnit;
    }

    public void setMcvUnit(String mcvUnit) {
        this.mcvUnit = mcvUnit;
    }

    public String getMchUnit() {
        return mchUnit;
    }

    public void setMchUnit(String mchUnit) {
        this.mchUnit = mchUnit;
    }

    public String getMchcUnit() {
        return mchcUnit;
    }

    public void setMchcUnit(String mchcUnit) {
        this.mchcUnit = mchcUnit;
    }

    public String getRdwUnit() {
        return rdwUnit;
    }

    public void setRdwUnit(String rdwUnit) {
        this.rdwUnit = rdwUnit;
    }

    //    public boolean rangeCheck(){
//        RangeValidator rangeValidator = new RangeValidator();
//        return rangeValidator.bloodCounts(hb, hbUnit, rbc, rbcUnit, wbc, wbcUnit, platelets, plateletsUnit, sex);
//    }
}
