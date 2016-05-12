package com.example.inspection.models;

import java.io.Serializable;
import java.util.Date;

public class Appointment implements Serializable {

    private String id;
    private String status;
    private String remark;
    private String taskID;
    private String flatBlock;
    private String building;
    private String district;
    private Date date;
    private String empID;
    private String empName;
    private Customer customer;

    public Appointment() {}

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Appointment(String id, String status, String remark, String taskID
        , String flatBlock, String building, String district, Date date, String empID, String empName, Customer customer) {
        this.id = id;
        this.status = status;
        this.remark = remark;
        this.taskID = taskID;
        this.flatBlock = flatBlock;
        this.building = building;
        this.district = district;
        this.date = date;
        this.empID = empID;
        this.empName = empName;
        this.customer = customer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTaskID() { return taskID; }

    public void setTaskID(String taskID) { this.taskID = taskID; }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getFlatBlock() {
        return flatBlock;
    }

    public void setFlatBlock(String flatBlock) {
        this.flatBlock = flatBlock;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) { this.building = building; }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
