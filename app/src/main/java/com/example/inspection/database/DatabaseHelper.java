package com.example.inspection.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.Schedule;
import com.example.inspection.models.Task;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String dbName="fypDB";
    static final String CustomerTable="Customer";
    static final String colCustomerID="CustomerID";
    static final String colCustomerJson="CustomerJson";

    static final String appointmentTable="Appointment";
    static final String colAppointmentID="AppointmentID";
    static final String colAppointmentJson="AppointmentJson";
    static final String colCustomer="Customer";

    static final String taskTable="Task";
    static final String colTaskID="TaskID";
    static final String colTaskJson="TaskJson";
    static final String colAppointment="Appointment";

    static final String scheduleTable="Schedule";
    static final String colScheduleID="ScheduleID";
    static final String colScheduleJson="ScheduleJson";

    public DatabaseHelper(Context context){
        super(context, dbName, null, 1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE "+CustomerTable+" ("+
                colCustomerID+" TEXT PRIMARY KEY , "+
                colCustomerJson+" TEXT );");
        db.execSQL("CREATE TABLE "+appointmentTable+" ("+
                colAppointmentID+" TEXT PRIMARY KEY , "+
                colAppointmentJson+" TEXT , "+
                colCustomer+" TEXT NOT NULL , FOREIGN KEY ("+colCustomer+") REFERENCES"+
                CustomerTable+" ("+colCustomerID+"));");
        db.execSQL("CREATE TABLE " + taskTable + " (" +
                colTaskID + " TEXT PRIMARY KEY , " +
                colTaskJson + " TEXT , " +
                colAppointment + " TEXT NOT NULL , FOREIGN KEY (" + colAppointment + ") REFERENCES" +
                appointmentTable + " (" + colAppointmentID + "));");
        db.execSQL("CREATE TABLE " + scheduleTable + " (" +
                colScheduleID + " TEXT PRIMARY KEY , " +
                colScheduleJson + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String customerToJson(Customer customer){
        Gson gson = new Gson();
        return gson.toJson(customer);
    }

    public Customer jsonToCustomer(String json){
        Gson gson = new Gson();
        return (Customer) gson.fromJson(json, Customer.class);
    }

    public String appointmentToJson(Appointment appointment){
        Gson gson = new Gson();
        return gson.toJson(appointment);
    }

    public Appointment jsonToAppointment(String json){
        Gson gson = new Gson();
        return (Appointment) gson.fromJson(json, Appointment.class);
    }

    public String taskToJson(Task task){
        Gson gson = new Gson();
        return gson.toJson(task);
    }

    public Task jsonToTask(String json){
        Gson gson = new Gson();
        return (Task) gson.fromJson(json, Task.class);
    }

    public String scheduleToJson(Schedule schedule){
        Gson gson = new Gson();
        return gson.toJson(schedule);
    }

    public Schedule jsonToSchedule(String json){
        Gson gson = new Gson();
        return (Schedule) gson.fromJson(json, Schedule.class);
    }
}
