package com.example.inspection.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.inspection.database.DatabaseHelper;
import com.example.inspection.dbmodels.WebAppointment;
import com.example.inspection.models.Appointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sur.Vival on 7/5/2016.
 */
public class WebAppointmentDAO {

    // 表格名稱
    public static final String TABLE_NAME = "WebAppointment";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "id";

    // 其它表格欄位名稱
    public static final String NAME_COLUMN = "name";
    public static final String PHONE_COLUMN = "phone";
    public static final String BUILDING_COLUMN = "building";
    public static final String BLOCK_COLUMN = "block";
    public static final String DATE_COLUMN = "date";
    public static final String REMARK_COLUMN = "remark";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME_COLUMN + " TEXT NOT NULL, " +
                    PHONE_COLUMN + " TEXT NOT NULL, " +
                    BUILDING_COLUMN + " TEXT NOT NULL, " +
                    BLOCK_COLUMN + " TEXT NOT NULL, " +
                    DATE_COLUMN + " TEXT NOT NULL, " +
                    REMARK_COLUMN + " TEXT)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public WebAppointmentDAO(Context context) {
        db = DatabaseHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增記錄
    public WebAppointment insert(WebAppointment app) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(NAME_COLUMN, app.getName());
        cv.put(PHONE_COLUMN, app.getPhone());
        cv.put(BUILDING_COLUMN, app.getBuilding());
        cv.put(BLOCK_COLUMN, app.getBlock());
        cv.put(DATE_COLUMN, app.getDate());
        cv.put(REMARK_COLUMN, app.getRemark());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        app.setId(id);
        // 回傳結果
        return app;
    }

    // 修改記錄
    public boolean update(WebAppointment app) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(NAME_COLUMN, app.getName());
        cv.put(PHONE_COLUMN, app.getPhone());
        cv.put(BUILDING_COLUMN, app.getBuilding());
        cv.put(BLOCK_COLUMN, app.getBlock());
        cv.put(DATE_COLUMN, app.getDate());
        cv.put(REMARK_COLUMN, app.getRemark());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + app.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定記錄
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // 讀取所有資料
    public List<WebAppointment> getAll() {
        List<WebAppointment> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public WebAppointment get(long id) {
        // 準備回傳結果用的物件
        WebAppointment app = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (cursor.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            app = getRecord(cursor);
        }

        // 關閉Cursor物件
        cursor.close();
        // 回傳結果
        return app;
    }

    // 把Cursor目前的資料包裝為物件
    public WebAppointment getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        WebAppointment app = new WebAppointment();

        app.setId(cursor.getLong(0));
        app.setName(cursor.getString(1));
        app.setPhone(cursor.getString(2));
        app.setBuilding(cursor.getString(3));
        app.setBlock(cursor.getString(4));
        app.setDate(cursor.getString(5));
        app.setRemark(cursor.getString(6));

        // 回傳結果
        return app;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }

    public void sampleData() {
        WebAppointment w1 = new WebAppointment(0,"Alex", "67612428", "Hong Kong", "4B32", "2016-05-07", "Afternoon");
        insert(w1);
    }

}
