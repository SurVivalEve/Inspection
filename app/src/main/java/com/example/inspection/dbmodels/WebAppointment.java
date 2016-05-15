package com.example.inspection.dbmodels;

import java.io.Serializable;

/**
 * Created by Sur.Vival on 7/5/2016.
 */
public class WebAppointment implements Serializable {
    public long id;
    public String appId;
    public String name;
    public String phone;
    public String building;
    public String block;
    public String date;
    public String remark;
    public String email;

    public WebAppointment() {
    }

    public WebAppointment(String appId, String name, String phone, String building, String block, String date, String remark, String email) {
        this.appId = appId;
        this.name = name;
        this.phone = phone;
        this.building = building;
        this.block = block;
        this.date = date;
        this.remark = remark;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
