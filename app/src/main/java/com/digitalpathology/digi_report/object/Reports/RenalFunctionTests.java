package com.digitalpathology.digi_report.object.Reports;

public class RenalFunctionTests {

    float bloodUrea, bloodUreaNitrogen, serumCreatinine, serumUricAcid;
    /**
     * taking all the units common for now i.e. mg/dl
     */
    String commonUnit;

    public RenalFunctionTests() {
    }

    public RenalFunctionTests(float bloodUrea, float bloodUreaNitrogen, float serumCreatinine, float serumUricAcid, String commonUnit) {
        this.bloodUrea = bloodUrea;
        this.bloodUreaNitrogen = bloodUreaNitrogen;
        this.serumCreatinine = serumCreatinine;
        this.serumUricAcid = serumUricAcid;
        this.commonUnit = commonUnit;
    }

    public float getBloodUrea() {
        return bloodUrea;
    }

    public void setBloodUrea(float bloodUrea) {
        this.bloodUrea = bloodUrea;
    }

    public float getBloodUreaNitrogen() {
        return bloodUreaNitrogen;
    }

    public void setBloodUreaNitrogen(float bloodUreaNitrogen) {
        this.bloodUreaNitrogen = bloodUreaNitrogen;
    }

    public float getSerumCreatinine() {
        return serumCreatinine;
    }

    public void setSerumCreatinine(float serumCreatinine) {
        this.serumCreatinine = serumCreatinine;
    }

    public float getSerumUricAcid() {
        return serumUricAcid;
    }

    public void setSerumUricAcid(float serumUricAcid) {
        this.serumUricAcid = serumUricAcid;
    }

    public String getCommonUnit() {
        return commonUnit;
    }

    public void setCommonUnit(String commonUnit) {
        this.commonUnit = commonUnit;
    }
}
