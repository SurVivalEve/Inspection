package com.example.inspection.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule implements Serializable {

    private List<Appointment> appointments;
    private int[] assign = new int[31];
    private int[] notAssign = new int[31];

    public Schedule(){
        appointments = new ArrayList<Appointment>();
        for(int i=0; i<31; i++){
            assign[i] = 0;
            notAssign[i] = 0;
        }
    }

    public Schedule(List<Appointment> appointments, int[] assign, int[] notAssign){
        this.appointments = appointments;
        this.assign = assign;
        this.notAssign = notAssign;
    }

    public void setAppointments(List<Appointment> appointments){
        this.appointments = appointments;
    }

    public List<Appointment> getAppointments(){
        return this.appointments;
    }

    public void setAssign(int[] assign){
        this.assign = assign;
    }

    public int[] getAssign(){
        return this.assign;
    }

    public void setNotAssign(int[] notAssign){
        this.notAssign = notAssign;
    }

    public int[] getNotAssign(){
        return this.notAssign;
    }
}
