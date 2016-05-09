package com.example.inspection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.inspection.models.Schedule;
import com.example.inspection.sync.SyncManager;

public class Login extends Activity {

    private EditText eT_username, eT_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        eT_username = (EditText) findViewById(R.id.eT_username);
        eT_password = (EditText) findViewById(R.id.eT_password);

        Drawable ic_login_person = getResources().getDrawable(R.drawable.ic_login_person);
        Drawable ic_login_password = getResources().getDrawable(R.drawable.ic_login_password);
        ic_login_person.setBounds(0, 0, (int)(ic_login_person.getIntrinsicWidth()*0.5), (int)(ic_login_person.getIntrinsicHeight()*0.5));
        ic_login_password.setBounds(0, 0, (int)(ic_login_password.getIntrinsicWidth()*0.5), (int)(ic_login_password.getIntrinsicHeight()*0.5));
        eT_username.setCompoundDrawables(ic_login_person, null, null, null);
        eT_password.setCompoundDrawables(ic_login_password, null, null, null);
    }

    public void doLogin(View view) {
            Intent i = new Intent(this, MainMenu.class);
            i.putExtra("empID", "E00000000006");
            startActivity(i);
//        if(eT_username.getText().toString().equalsIgnoreCase("")||eT_password.getText().toString().equalsIgnoreCase("")){
//            Toast.makeText(this, "Please Enter username and password", Toast.LENGTH_LONG).show();
//        } else {
//            new getLoginResult().execute(eT_username.getText().toString(), eT_password.getText().toString());
//        }
    }

    public void login(String empID) {
        if(!empID.equalsIgnoreCase("false") && empID.substring(0 ,1).equalsIgnoreCase("E")) {
            Intent i = new Intent(this, MainMenu.class);
            i.putExtra("empID", empID);
            startActivity(i);
        } else {
            Toast.makeText(this, "Wrong username or password!", Toast.LENGTH_LONG).show();
        }
    }

    private class getLoginResult extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            SyncManager syncManager = new SyncManager("login.php");
            return syncManager.syncLogin(params[0],params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            login(result);
        }
    }
}