package com.example.inspection;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Path;
import android.os.AsyncTask;

import com.example.inspection.sync.SyncManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Blob;

/**
 * Created by Kaz on 24/4/2016.
 */
public class DrawTask extends AsyncTask< Void, Void, String> {
    Context context;
    //SubButton btn;
    ProgressDialog progressDialog;
    int id;
    String pathsJson;
    String paintsColorJson;
    String paintsWidthJson;
    String paintsEffectJson;
    String bitmapString;
    String name = "one";
    String action;
    /**
     * test store path?
     http://www.java2s.com/Code/Java/Database-SQL-JDBC/HowtoserializedeserializeaJavaobjecttotheMySQLdatabase.htm


     using hashmap to save a arraylist of path and order for id
     may save another for actucl width height for each path

     make asyncTask
     */


    public DrawTask(Context context, int id, String action){
        this.context = context;
        this.id = id;
        this.action = action;
    }
    public DrawTask(Context context, String pathsJson, String paintsColorJson, String paintsWidthJson, String paintsEffectJson, String bitmapString, String action) {
        this.context = context;
        //this.btn = btn;
        this.pathsJson = pathsJson;
        this.paintsColorJson = paintsColorJson;
        this.paintsWidthJson = paintsWidthJson;
        this.paintsEffectJson = paintsEffectJson;
        this.bitmapString = bitmapString;
        this.action = action;
    }
    public DrawTask(String action) {
        this.action = action;
    }

    @Override
    protected void onPreExecute() {
//        progressDialog = new ProgressDialog((context));
//        progressDialog.setTitle("Loading ");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMax(100);
//        progressDialog.setProgress(0);
//        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
//        //network things
//        CustomPath path = new CustomPath(this.path);
//        //Blob customPath = null;
//        try {
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
//            objectOutputStream.writeObject(path);
//
//            objectOutputStream.flush();
//            objectOutputStream.close();
//            byte[] bs = byteArrayOutputStream.toByteArray();
//            //customPath = new SerialBlob();
//
//            SyncManager syncManager = new SyncManager("drawer.php?path=" + path + "&name=" + name);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if(action.equals("save")) {
            SyncManager syncManager = new SyncManager("drawer.php");
            syncManager.syncDraw(pathsJson, paintsColorJson, paintsWidthJson, paintsEffectJson, bitmapString, name);
            return "";
        } else if(action.equals("load")) {
            SyncManager syncManager = new SyncManager("drawer.php");
            return syncManager.loadDraw(id);
        } else {
            SyncManager syncManager = new SyncManager("initDrawNag.php");
            return  syncManager.initDrawNag();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}