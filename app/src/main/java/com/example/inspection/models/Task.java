package com.example.inspection.models;

public class Task {

    private String id;
    private String status;
    private String title;
    private String remark;
    private Appointment appointment;

    public Task() {}

    public Task(String id, String status, String title, String remark, Appointment appointment) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.remark = remark;
        this.appointment = appointment;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
