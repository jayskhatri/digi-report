package com.digitalpathology.digi_report.object;

class MedicalReport {

    String medReportName;
    String uploadDate;

    public MedicalReport(String medReportName, String uploadDate) {
        this.medReportName = medReportName;
        this.uploadDate = uploadDate;
    }

    public String getMedReportName() {
        return medReportName;
    }

    public void setMedReportName(String medReportName) {
        this.medReportName = medReportName;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
