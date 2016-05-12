package com.example.inspection.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.example.inspection.dao.LocalNextMonthScheduleDAO;
import com.example.inspection.dao.LocalPreMonthScheduleDAO;
import com.example.inspection.dao.LocalScheduleDAO;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.WebAppointment;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "fyp.db";
    public static final int VERSION = 1;
    private static SQLiteDatabase db;

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context){
        if(db == null || !db.isOpen()){
            db = new DatabaseHelper(context,
                                    DATABASE_NAME,
                                    null,
                                    VERSION).getWritableDatabase();
        }
        return db;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WebAppointmentDAO.CREATE_TABLE);
        db.execSQL(LocalScheduleDAO.CREATE_TABLE);
        db.execSQL(LocalPreMonthScheduleDAO.CREATE_TABLE);
        db.execSQL(LocalNextMonthScheduleDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 刪除原有的表格
        db.execSQL("DROP TABLE IF EXISTS " + WebAppointmentDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocalScheduleDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocalPreMonthScheduleDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocalNextMonthScheduleDAO.TABLE_NAME);
        // 呼叫onCreate建立新版的表格
        onCreate(db);
    }
}
