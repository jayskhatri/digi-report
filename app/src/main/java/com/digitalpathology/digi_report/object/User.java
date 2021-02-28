package com.digitalpathology.digi_report.object;

class User {

    String name;
    String email;
    String phone;
    Integer numberOfReportsUploaded;

    public User(String name, String email, String phone, Integer numberOfReportsUploaded) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.numberOfReportsUploaded = numberOfReportsUploaded;
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

    public Integer getNumberOfReportsUploaded() {
        return numberOfReportsUploaded;
    }

    public void setNumberOfReportsUploaded(Integer numberOfReportsUploaded) {
        this.numberOfReportsUploaded = numberOfReportsUploaded;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
