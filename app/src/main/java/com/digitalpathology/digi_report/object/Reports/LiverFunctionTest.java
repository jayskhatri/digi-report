package com.digitalpathology.digi_report.object.Reports;

public class LiverFunctionTest {

    float SBilirubinTotal, SBilirubinDirect, SBilirubinIndirect, SGPT;
    float SAlkalinePhosphatse, SGOT, serumPotiTotal, serumPotiAlbumin, serumPotiGlobulins, serumPotiAGRatio;
    String SBilirubinUnit, serumProtiUnit, sgptUnit, sgotUnit, SAlkalinePhosphatseUnit;

    public LiverFunctionTest() {
    }

    public LiverFunctionTest(float SBilirubinTotal, float SBilirubinDirect, float SBilirubinIndirect, float SGPT,
                             float SAlkalinePhosphatse, float SGOT, float serumPotiTotal, float serumPotiAlbumin,
                             float serumPotiGlobulins, float serumPotiAGRatio, String SBilirubinUnit, String serumProtiUnit,
                             String sgptUnit, String sgotUnit, String SAlkalinePhosphatseUnit) {
        this.SBilirubinTotal = SBilirubinTotal;
        this.SBilirubinDirect = SBilirubinDirect;
        this.SBilirubinIndirect = SBilirubinIndirect;
        this.SGPT = SGPT;
        this.SAlkalinePhosphatse = SAlkalinePhosphatse;
        this.SGOT = SGOT;
        this.serumPotiTotal = serumPotiTotal;
        this.serumPotiAlbumin = serumPotiAlbumin;
        this.serumPotiGlobulins = serumPotiGlobulins;
        this.serumPotiAGRatio = serumPotiAGRatio;
        this.SBilirubinUnit = SBilirubinUnit;
        this.serumProtiUnit = serumProtiUnit;
        this.sgptUnit = sgptUnit;
        this.sgotUnit = sgotUnit;
        this.SAlkalinePhosphatseUnit = SAlkalinePhosphatseUnit;
    }

    public float getSBilirubinTotal() {
        return SBilirubinTotal;
    }

    public void setSBilirubinTotal(float SBilirubinTotal) {
        this.SBilirubinTotal = SBilirubinTotal;
    }

    public float getSBilirubinDirect() {
        return SBilirubinDirect;
    }

    public void setSBilirubinDirect(float SBilirubinDirect) {
        this.SBilirubinDirect = SBilirubinDirect;
    }

    public float getSBilirubinIndirect() {
        return SBilirubinIndirect;
    }

    public void setSBilirubinIndirect(float SBilirubinIndirect) {
        this.SBilirubinIndirect = SBilirubinIndirect;
    }

    public float getSGPT() {
        return SGPT;
    }

    public void setSGPT(float SGPT) {
        this.SGPT = SGPT;
    }

    public float getSAlkalinePhosphatse() {
        return SAlkalinePhosphatse;
    }

    public void setSAlkalinePhosphatse(float SAlkalinePhosphatse) {
        this.SAlkalinePhosphatse = SAlkalinePhosphatse;
    }

    public float getSGOT() {
        return SGOT;
    }

    public void setSGOT(float SGOT) {
        this.SGOT = SGOT;
    }

    public float getSerumPotiTotal() {
        return serumPotiTotal;
    }

    public void setSerumPotiTotal(float serumPotiTotal) {
        this.serumPotiTotal = serumPotiTotal;
    }

    public float getSerumPotiAlbumin() {
        return serumPotiAlbumin;
    }

    public void setSerumPotiAlbumin(float serumPotiAlbumin) {
        this.serumPotiAlbumin = serumPotiAlbumin;
    }

    public float getSerumPotiGlobulins() {
        return serumPotiGlobulins;
    }

    public void setSerumPotiGlobulins(float serumPotiGlobulins) {
        this.serumPotiGlobulins = serumPotiGlobulins;
    }

    public float getSerumPotiAGRatio() {
        return serumPotiAGRatio;
    }

    public void setSerumPotiAGRatio(float serumPotiAGRatio) {
        this.serumPotiAGRatio = serumPotiAGRatio;
    }

    public String getSBilirubinUnit() {
        return SBilirubinUnit;
    }

    public void setSBilirubinUnit(String SBilirubinUnit) {
        this.SBilirubinUnit = SBilirubinUnit;
    }

    public String getSerumProtiUnit() {
        return serumProtiUnit;
    }

    public void setSerumProtiUnit(String serumProtiUnit) {
        this.serumProtiUnit = serumProtiUnit;
    }

    public String getSgptUnit() {
        return sgptUnit;
    }

    public void setSgptUnit(String sgptUnit) {
        this.sgptUnit = sgptUnit;
    }

    public String getSgotUnit() {
        return sgotUnit;
    }

    public void setSgotUnit(String sgotUnit) {
        this.sgotUnit = sgotUnit;
    }

    public String getSAlkalinePhosphatseUnit() {
        return SAlkalinePhosphatseUnit;
    }

    public void setSAlkalinePhosphatseUnit(String SAlkalinePhosphatseUnit) {
        this.SAlkalinePhosphatseUnit = SAlkalinePhosphatseUnit;
    }
}
