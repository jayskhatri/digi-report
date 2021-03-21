package com.digitalpathology.digi_report.object;

public class RangeValidator {

    //bloodsugar range - 70 - 140
    private boolean bloodsugarRangeValidator(float bloodsugarlevel, String unit){
        if(unit.contentEquals("mg/dl")){
            return bloodsugarlevel >= 70f && bloodsugarlevel <= 140f;
        }
        return false;
    }

    /** function name: bloodCounts
     *
     * @param hb
     * @param hbunit
     * @param rbc
     * @param rbcUnit
     * @param wbc
     * @param wbcUnit
     * @param platelets
     * @param plateletsUnit
     * @param sex
     * @return type: boolean, checks all the blood counts ranges i.e. hb, wbc, rbc, platelets
     */
    public boolean bloodCounts(float hb, String hbunit, float rbc, String rbcUnit, float wbc, String wbcUnit, float platelets, String plateletsUnit, String sex){
        return (HBvalidator(hb, sex) && RBCvalidator(rbc, sex) && WBCvalidator(wbc) && plateletvalidator(platelets));
    }

    /** Function Name: HBvalidator
     *
     * @param hb
     * @param sex
     * @return type: boolean - "true" - if HBvalidation succeeds, otherwise "false" - if HB validation does not succeed
     */
    private boolean HBvalidator(float hb, String sex){

            switch(sex){
                case "male":
                    return (hb>=14 && hb<=18);

                case "female":
                    return (hb>=12 && hb<=16);

                default:
                    return false;
            }
    }

    /** function name: RBCvalidator
     *
     * @param rbc
     * @param sex
     * @return type: boolean - "true" - if RBCvalidation succeeds, otherwise "false" - if RBC validation does not succeed
     */
    private boolean RBCvalidator(float rbc, String sex){
//        if(unit.contentEquals("mill/cmm")){
            switch(sex){
                case "male":
                    return (rbc>=4.5 && rbc<=5.5);

                case "female":
                    return (rbc>=3.8 && rbc<=5.2);

                default:
                    return false;
            }
    }

    /** function name: WBCvalidator
     *
     * @param wbc
     * @return type: string - "true" - if WBC validation succeeds, otherwise "false" - if WBC validation does not succeed
     */
    private boolean WBCvalidator(float wbc){
            return (wbc>=4000 && wbc<=10000);
    }

    /** function name: plateletvalidator
     *
     * @param platelet
     * @return type: string - "true" - if platelatevalidation succeeds, otherwise "false" - if platelets validation does not succeed & "Unit is not matching" - if unit does not match
     */
    private boolean plateletvalidator(float platelet){
            return (platelet>=1.5 && platelet<=4.5);
    }


}
