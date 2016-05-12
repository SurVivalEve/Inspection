package com.example.inspection.dbmodels;

import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalSchedule implements Serializable {

    public long id;
    public String appointmentID;
    public String astatus;
    public String aremark;
    public String taskID;
    public String flatBlock;
    public String building;
    public String districtEN;
    public String appointmentTime;
    public String empID;
    public String empName;
    public String custID;
    public String custFullName;
    public String custPhone;

    public LocalSchedule() {
    }

    public LocalSchedule(String appointmentID, String astatus, String aremark, String taskID, String flatBlock,
                         String building, String districtEN, String appointmentTime, String empID, String empName,
                         String custID, String custFullName, String custPhone) {
        this.appointmentID = appointmentID;
        this.astatus = astatus;
        this.aremark = aremark;
        this.taskID = taskID;
        this.flatBlock = flatBlock;
        this.building = building;
        this.districtEN = districtEN;
        this.appointmentTime = appointmentTime;
        this.empID = empID;
        this.empName = empName;
        this.custID = custID;
        this.custFullName = custFullName;
        this.custPhone = custPhone;
    }

    public LocalSchedule(long id, String appointmentID, String astatus, String aremark, String taskID, String flatBlock,
                         String building, String districtEN, String appointmentTime, String empID, String empName,
                         String custID, String custFullName, String custPhone) {
        this.id = id;
        this.appointmentID = appointmentID;
        this.astatus = astatus;
        this.aremark = aremark;
        this.taskID = taskID;
        this.flatBlock = flatBlock;
        this.building = building;
        this.districtEN = districtEN;
        this.appointmentTime = appointmentTime;
        this.empID = empID;
        this.empName = empName;
        this.custID = custID;
        this.custFullName = custFullName;
        this.custPhone = custPhone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getAstatus() {
        return astatus;
    }

    public void setAstatus(String astatus) {
        this.astatus = astatus;
    }

    public String getAremark() {
        return aremark;
    }

    public void setAremark(String aremark) {
        this.aremark = aremark;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
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

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getDistrictEN() {
        return districtEN;
    }

    public void setDistrictEN(String districtEN) {
        this.districtEN = districtEN;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

    public String getCustFullName() {
        return custFullName;
    }

    public void setCustFullName(String custFullName) {
        this.custFullName = custFullName;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }
}
