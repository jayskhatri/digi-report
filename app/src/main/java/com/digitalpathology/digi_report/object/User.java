package com.digitalpathology.digi_report.object;

public class User {

    String uid;
    String name;
    String email;
    String phone;
    int numberOfReportsUploaded;
    MedicalReport medreport;

    public User(String uid, String name, String email, String phone, int numberOfReportsUploaded) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.numberOfReportsUploaded = numberOfReportsUploaded;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getNumberOfReportsUploaded() {
        return numberOfReportsUploaded;
    }

    public void setNumberOfReportsUploaded(int numberOfReportsUploaded) {
        this.numberOfReportsUploaded = numberOfReportsUploaded;
    }

    public void addreport(){
        this.setNumberOfReportsUploaded(this.getNumberOfReportsUploaded() + 1);
    }

    public void removereport(){
        this.setNumberOfReportsUploaded(this.getNumberOfReportsUploaded() -1);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
