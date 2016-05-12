package com.example.inspection.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.inspection.database.DatabaseHelper;
import com.example.inspection.dbmodels.LocalSchedule;
import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.Schedule;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocalPreMonthScheduleDAO implements Serializable {

    public static final String TABLE_NAME = "LocalPreMonthSchedule";

    public static final String KEY_ID = "id";

    public static final String APPOINTMENTID_COLUMN = "appointmentID";
    public static final String ASTATUS_COLUMN = "astatus";
    public static final String AREMARK_COLUMN = "aremark";
    public static final String TASKID_COLUMN = "taskID";
    public static final String FLATBLOCK_COLUMN = "flatBlock";
    public static final String BUILDING_COLUMN = "building";
    public static final String DISTRICTEN_COLUMN = "districtEN";
    public static final String APPOINTMENTTIME_COLUMN = "appointmentTime";
    public static final String EMPID_COLUMN = "empID";
    public static final String EMPNAME_COLUMN = "empName";
    public static final String CUSTID_COLUMN = "custID";
    public static final String CUSTFULLNAME_COLUMN = "custFullName";
    public static final String CUSTPHONE_COLUMN = "custPhone";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    APPOINTMENTID_COLUMN + " TEXT NOT NULL, " +
                    ASTATUS_COLUMN + " TEXT NOT NULL, " +
                    AREMARK_COLUMN + " TEXT NOT NULL, " +
                    TASKID_COLUMN + " TEXT, " +
                    FLATBLOCK_COLUMN + " TEXT NOT NULL, " +
                    BUILDING_COLUMN + " TEXT NOT NULL, " +
                    DISTRICTEN_COLUMN + " TEXT NOT NULL, " +
                    APPOINTMENTTIME_COLUMN + " TEXT NOT NULL, " +
                    EMPID_COLUMN + " TEXT, " +
                    EMPNAME_COLUMN + " TEXT, " +
                    CUSTID_COLUMN + " TEXT NOT NULL, " +
                    CUSTFULLNAME_COLUMN + " TEXT NOT NULL, " +
                    CUSTPHONE_COLUMN + " TEXT NOT NULL)";

    private SQLiteDatabase db;

    public LocalPreMonthScheduleDAO(Context context) { db = DatabaseHelper.getDatabase(context); }

    public void close() {
        db.close();
    }

    public LocalSchedule insert(LocalSchedule sch) {
        ContentValues cv = new ContentValues();

        cv.put(APPOINTMENTID_COLUMN, sch.getAppointmentID());
        cv.put(ASTATUS_COLUMN, sch.getAstatus());
        cv.put(AREMARK_COLUMN, sch.getAremark());
        cv.put(TASKID_COLUMN, sch.getTaskID());
        cv.put(FLATBLOCK_COLUMN, sch.getFlatBlock());
        cv.put(BUILDING_COLUMN, sch.getBuilding());
        cv.put(DISTRICTEN_COLUMN, sch.getDistrictEN());
        cv.put(APPOINTMENTTIME_COLUMN, sch.getAppointmentTime());
        cv.put(EMPID_COLUMN, sch.getEmpID());
        cv.put(EMPNAME_COLUMN, sch.getEmpName());
        cv.put(CUSTID_COLUMN, sch.getCustID());
        cv.put(CUSTFULLNAME_COLUMN, sch.getCustFullName());
        cv.put(CUSTPHONE_COLUMN, sch.getCustPhone());

        long id = db.insert(TABLE_NAME, null, cv);

        sch.setId(id);

        return sch;
    }

    public boolean update(LocalSchedule sch) {
        ContentValues cv = new ContentValues();

        cv.put(APPOINTMENTID_COLUMN, sch.getAppointmentID());
        cv.put(ASTATUS_COLUMN, sch.getAstatus());
        cv.put(AREMARK_COLUMN, sch.getAremark());
        cv.put(TASKID_COLUMN, sch.getTaskID());
        cv.put(FLATBLOCK_COLUMN, sch.getFlatBlock());
        cv.put(BUILDING_COLUMN, sch.getBuilding());
        cv.put(DISTRICTEN_COLUMN, sch.getDistrictEN());
        cv.put(APPOINTMENTTIME_COLUMN, sch.getAppointmentTime());
        cv.put(EMPID_COLUMN, sch.getEmpID());
        cv.put(EMPNAME_COLUMN, sch.getEmpName());
        cv.put(CUSTID_COLUMN, sch.getCustID());
        cv.put(CUSTFULLNAME_COLUMN, sch.getCustFullName());
        cv.put(CUSTPHONE_COLUMN, sch.getCustPhone());

        String where = KEY_ID + "=" + sch.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public void delete(){
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public List<LocalSchedule> getAll() {
        List<LocalSchedule> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public Schedule toSchedule(List<LocalSchedule> sch){
        List<Appointment> result = new ArrayList<Appointment>();
        int[] assign = new int[31];
        int[] notAssign = new int[31];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0; i<sch.size(); i++) {
            String empID = "", taskID = "", empName = "";
            Date appointmentTime = new Date();
            try{
                appointmentTime = formatter.parse(sch.get(i).getAppointmentTime());
            } catch (ParseException e){
                Log.d("LocalSchduleDAO error", "date formatter error");
                Log.d("Detail", e.toString());
            }
            if (sch.get(i).getEmpID() !=null && !sch.get(i).getEmpID().isEmpty() && !sch.get(i).getEmpID().equalsIgnoreCase("null")) {
                empID = sch.get(i).getEmpID();
                empName = sch.get(i).getEmpName();
                assign[appointmentTime.getDate() - 1]++;
            } else {
                notAssign[appointmentTime.getDate() - 1]++;
            }
            if (sch.get(i).getTaskID() !=null && !sch.get(i).getTaskID().isEmpty() && !sch.get(i).getTaskID().equalsIgnoreCase("null"))
                taskID = sch.get(i).getTaskID();

            Customer cust = new Customer(
                    sch.get(i).getCustID(),
                    sch.get(i).getCustFullName(),
                    sch.get(i).getCustPhone(),
                    Customer.Sex.valueOf("M"),
                    ""//customer email
            );
            Appointment apt = new Appointment(
                    sch.get(i).getAppointmentID(),
                    sch.get(i).getAstatus(),
                    sch.get(i).getAremark(),
                    taskID,
                    sch.get(i).getFlatBlock(),
                    sch.get(i).getBuilding(),
                    sch.get(i).getDistrictEN(),
                    appointmentTime,
                    empID,
                    empName,
                    cust
            );
            result.add(apt);
        }
        return new Schedule(result, assign, notAssign);
    }

    public Schedule toSchedule(LocalSchedule sch){
        List<Appointment> result = new ArrayList<Appointment>();
        int[] assign = new int[31];
        int[] notAssign = new int[31];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String empID = "", taskID = "", empName = "";
        Date appointmentTime = new Date();
        try{
            appointmentTime = formatter.parse(sch.getAppointmentTime());
        } catch (ParseException e){
            Log.d("LocalSchduleDAO error", "date formatter error");
            Log.d("Detail", e.toString());
        }
        if (sch.getEmpID() !=null && !sch.getEmpID().isEmpty() && !sch.getEmpID().equalsIgnoreCase("null")) {
            empID = sch.getEmpID();
            empName = sch.getEmpName();
            assign[appointmentTime.getDate() - 1]++;
        } else {
            notAssign[appointmentTime.getDate() - 1]++;
        }
        if (sch.getTaskID() !=null && !sch.getTaskID().isEmpty() && !sch.getTaskID().equalsIgnoreCase("null"))
            taskID = sch.getTaskID();

        Customer cust = new Customer(
                sch.getCustID(),
                sch.getCustFullName(),
                sch.getCustPhone(),
                Customer.Sex.valueOf("M"),
                ""//customer email
        );
        Appointment apt = new Appointment(
                sch.getAppointmentID(),
                sch.getAstatus(),
                sch.getAremark(),
                taskID,
                sch.getFlatBlock(),
                sch.getBuilding(),
                sch.getDistrictEN(),
                appointmentTime,
                empID,
                empName,
                cust
        );
        result.add(apt);

        return new Schedule(result, assign, notAssign);
    }

    public LocalSchedule get(long id) {
        LocalSchedule result = null;
        String where = KEY_ID + "=" + id;
        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            result = getRecord(cursor);
        }

        cursor.close();
        return result;
    }

    public LocalSchedule getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        LocalSchedule sch = new LocalSchedule();

        sch.setId(cursor.getLong(0));
        sch.setAppointmentID(cursor.getString(1));
        sch.setAstatus(cursor.getString(2));
        sch.setAremark(cursor.getString(3));
        sch.setTaskID(cursor.getString(4));
        sch.setFlatBlock(cursor.getString(5));
        sch.setBuilding(cursor.getString(6));
        sch.setDistrictEN(cursor.getString(7));
        sch.setAppointmentTime(cursor.getString(8));
        sch.setEmpID(cursor.getString(9));
        sch.setEmpName(cursor.getString(10));
        sch.setCustID(cursor.getString(11));
        sch.setCustFullName(cursor.getString(12));
        sch.setCustPhone(cursor.getString(13));

        return sch;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }

    public void sampleData() {
        LocalSchedule sch = new LocalSchedule(0, "A00000000024", "V", "123", "T00000000005", "3", "Chinachem Exchange Square," +
                " 1 Hoi Wan Street, Quarry Bay", "Wan Chai District", "2016-03-02 10:00:00", "E00000000006",
                "Sin Chi Tak", "C00000000002", "Fong Tak Wah", "96230972");
        insert(sch);
    }
}
