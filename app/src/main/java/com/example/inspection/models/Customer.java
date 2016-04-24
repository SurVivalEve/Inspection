package com.example.inspection.models;

public class Customer {
    public enum Sex {
        M, F;
    }

    private String id;
    private String fullname;
    private String phone;
    private Sex sex;
    private String email;

    public Customer() {}
    
    public Customer(String id, String fullname, String phone, Sex sex, String email) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
        this.sex = sex;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
