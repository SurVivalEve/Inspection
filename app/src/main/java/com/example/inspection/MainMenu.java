package com.example.inspection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.models.RecentJob;
import com.example.inspection.service.AppointmentService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, QuotationOrderForm.QuotationsListener, QuotationInvoice.QuotationsListener, DrawFragment.QuotationsListener {

    private static String empID = "";
    private TextView staffID;
    private ToggleButton toggleButton;

    // Local Database init
    private WebAppointmentDAO webAppDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

      empID = getIntent().getExtras().getString("empID");
//        empID = "E00000000006";

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        staffID = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtStaffID);
        staffID.setText(empID);

        setDefaultFragment();
        setDatabase();


        // Test Service (For Debug use)
//        toggleButton = (ToggleButton) findViewById(R.id.service);
//        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//            Intent service = new Intent(MainMenu.this,AppointmentService.class);
//            Intent service2 = new Intent(MainMenu.this, TestService.class);
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b){
//                    getApplicationContext().startService(service2);
//                }else{
//                    getApplicationContext().stopService(service2);
//                }
//            }
//        });

        Intent intent = new Intent(this, AppointmentService.class);
        startService(intent);

    }

    @Override
    public void sendOrderFormMessage(JSONArray data) {
        QuotationsMenu quotationsMenu = (QuotationsMenu) getSupportFragmentManager().findFragmentByTag("quotationsMenu");
        quotationsMenu.setOrderForm(data);
    }

    @Override
    public void sendInvoicMessage(JSONArray data) {
        QuotationsMenu quotationsMenu = (QuotationsMenu) getSupportFragmentManager().findFragmentByTag("quotationsMenu");
        quotationsMenu.setInvoice(data);
    }

    private void setDatabase() {
        // 建立資料庫物件
        webAppDAO = new WebAppointmentDAO(getApplicationContext());

        // 如果資料庫是空的，就建立一些範例資料
        // 這是為了方便測試用的，完成應用程式以後可以拿掉
//        if(webAppDAO.getCount()==0)
//            webAppDAO.sampleData();


    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        RecentJobFragment recentJobFragment = RecentJobFragment.newInstance(1, empID);
        transaction.replace(R.id.main_fragment, recentJobFragment, "recentjob");
        transaction.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (item.getItemId()) {
            case R.id.nav_recent:
                ft.replace(R.id.main_fragment, getSupportFragmentManager().findFragmentByTag("recentjob"))
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_task:
                ft.replace(R.id.main_fragment, AddTaskFragment.newInstance(empID), "addtask")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_schedule:
                ft.replace(R.id.main_fragment, CalendarFragment.newInstance(empID), "schedule")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_quotations:
                ft.replace(R.id.main_fragment, QuotationsMenu.newInstance(empID), "quotationsMenu")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_appointment:
                ft.replace(R.id.main_fragment, AppointmentFragment.newInstance(empID), "appointment")
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.nav_custDetails:
//            ft.add(R.id.main_fragment, customerFragment, "customer");
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//                CustomerFragment customerFragment = new CustomerFragment();
//                ft.replace(R.id.main_fragment, customerFragment)
//                    .addToBackStack(null)
//                    .commit();
                break;
            case R.id.nav_custContact:

                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                Log.i("MainActivity", "popping backstack");
                fm.popBackStack();
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.op_menu_help:
                Toast.makeText(MainMenu.this, "HelloWorld", Toast.LENGTH_SHORT).show();
                break;
            default:
                return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        QuotationsMenu quotationsMenu = (QuotationsMenu) getSupportFragmentManager().findFragmentByTag("quotationsMenu");
        quotationsMenu.onActivityResult(requestCode, resultCode, data);
    }

    public static String getEmpID() {
        return empID;
    }

    public static void setEmpID(String empID) {
        MainMenu.empID = empID;
    }

    @Override
    public void sendGraphMessage(Bitmap bitmap) {
        QuotationsMenu quotationsMenu = (QuotationsMenu) getSupportFragmentManager().findFragmentByTag("quotationsMenu");
        quotationsMenu.setGraphList(bitmap);
    }
}
