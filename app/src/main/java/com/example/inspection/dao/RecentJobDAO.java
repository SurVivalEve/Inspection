package com.example.inspection.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.inspection.database.DatabaseHelper;
import com.example.inspection.dbmodels.LocalRecentJob;
import com.example.inspection.dbmodels.WebAppointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sur.Vival on 12/5/2016.
 */
public class RecentJobDAO {

    // 表格名稱
    public static final String TABLE_NAME = "RecentJob";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "id";

    // 其它表格欄位名稱
    public static final String TITLE_COLUMN = "title";
    public static final String REMARK_COLUMN = "remark";
    public static final String TSTATUS_COLUMN = "tstatus";
    public static final String PHONE_COLUMN = "phone";
    public static final String FULLNAME_COLUMN = "fullname";
    public static final String FLAT_BLOCK_COLUMN = "flatblock";
    public static final String BUILDEING_COLUMN = "building";
    public static final String DISTRICT_EN_COLUMN = "district";
    public static final String ISLAND_COLUMN = "island";
    public static final String WHICH_COLUMN = "which";
    public static final String APP_ID = "appid";
    public static final String APP_TIME = "apptime";
    public static final String EMAIL = "email";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE_COLUMN + " TEXT, " +
                    REMARK_COLUMN + " TEXT, " +
                    TSTATUS_COLUMN + " TEXT, " +
                    PHONE_COLUMN + " TEXT NOT NULL, " +
                    FULLNAME_COLUMN + " TEXT NOT NULL, " +
                    FLAT_BLOCK_COLUMN + " TEXT NOT NULL, " +
                    BUILDEING_COLUMN + " TEXT NOT NULL, " +
                    DISTRICT_EN_COLUMN + " TEXT NOT NULL, " +
                    ISLAND_COLUMN + " TEXT NOT NULL, " +
                    WHICH_COLUMN + " TEXT NOT NULL, " +
                    APP_ID + " TEXT NOT NULL, " +
                    APP_TIME + " TEXT NOT NULL, " +
                    EMAIL + " TEXT)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public RecentJobDAO(Context context) {
        db = DatabaseHelper.getDatabase(context);
    }

    // 關閉資料庫，一般的應用都不需要修改
    public void close() {
        db.close();
    }

    // 新增記錄
    public long insert(LocalRecentJob rj) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TITLE_COLUMN, rj.getTitle());
        cv.put(REMARK_COLUMN, rj.getRemark());
        cv.put(TSTATUS_COLUMN, rj.getTstatus());
        cv.put(PHONE_COLUMN, rj.getPhone());
        cv.put(FULLNAME_COLUMN, rj.getFullname());
        cv.put(FLAT_BLOCK_COLUMN, rj.getFlatBlock());
        cv.put(BUILDEING_COLUMN, rj.getBuilding());
        cv.put(DISTRICT_EN_COLUMN, rj.getDistrictEN());
        cv.put(ISLAND_COLUMN, rj.getIsland());
        cv.put(WHICH_COLUMN, rj.getWhich());
        cv.put(APP_ID, rj.getAppid());
        cv.put(APP_TIME, rj.getApptime());
        cv.put(EMAIL, rj.getEmail());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        rj.setId(id);
        // 回傳結果
        return id;
    }

    // 修改記錄
    public boolean update(LocalRecentJob rj) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(TITLE_COLUMN, rj.getTitle());
        cv.put(REMARK_COLUMN, rj.getRemark());
        cv.put(TSTATUS_COLUMN, rj.getTstatus());
        cv.put(PHONE_COLUMN, rj.getPhone());
        cv.put(FULLNAME_COLUMN, rj.getFullname());
        cv.put(FLAT_BLOCK_COLUMN, rj.getFlatBlock());
        cv.put(BUILDEING_COLUMN, rj.getBuilding());
        cv.put(DISTRICT_EN_COLUMN, rj.getDistrictEN());
        cv.put(ISLAND_COLUMN, rj.getIsland());
        cv.put(WHICH_COLUMN, rj.getWhich());
        cv.put(APP_ID, rj.getAppid());
        cv.put(APP_TIME, rj.getApptime());
        cv.put(EMAIL, rj.getEmail());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + rj.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // 刪除參數指定記錄
    public boolean delete(long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    public void delete(){
        db.execSQL("delete from "+ TABLE_NAME);
    }

    // 讀取所有資料
    public List<LocalRecentJob> getAll() {
        List<LocalRecentJob> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 讀取特定資料
    public List<LocalRecentJob> getAllByWhich(String which) {
        List<LocalRecentJob> result = new ArrayList<>();

        String where = WHICH_COLUMN + "=" + "'"+which+"'";

        Cursor cursor = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public LocalRecentJob get(long id) {
        // 準備回傳結果用的物件
        LocalRecentJob app = null;
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
    public LocalRecentJob getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        LocalRecentJob rj = new LocalRecentJob();

        rj.setId(cursor.getLong(0));
        rj.setTitle(cursor.getString(1));
        rj.setRemark(cursor.getString(2));
        rj.setTstatus(cursor.getString(3));
        rj.setPhone(cursor.getString(4));
        rj.setFullname(cursor.getString(5));
        rj.setFlatBlock(cursor.getString(6));
        rj.setBuilding(cursor.getString(7));
        rj.setDistrictEN(cursor.getString(8));
        rj.setIsland(cursor.getString(9));
        rj.setWhich(cursor.getString(10));
        rj.setAppid(cursor.getString(11));
        rj.setApptime(cursor.getString(12));
        rj.setEmail(cursor.getString(13));

        // 回傳結果
        return rj;
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
        LocalRecentJob r1 = new LocalRecentJob();
        insert(r1);
    }


}
