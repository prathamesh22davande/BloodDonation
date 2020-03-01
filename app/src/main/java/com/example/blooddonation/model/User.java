package com.example.blooddonation.model;

public class User {

    private String name,bloodGroup,address,phone,birthDate,gender,email;

    public User(){}


    public User(String name, String bloodGroup, String address, String phone, String birthDate, String gender, String email) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.email = email;
    }



    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
}
