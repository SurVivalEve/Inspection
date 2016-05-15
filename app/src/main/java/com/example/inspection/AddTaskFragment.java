package com.example.inspection;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.inspection.adapters.CustomerListAdapter;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.WebAppointment;
import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.sync.SyncManager;

import java.util.Calendar;
import java.util.List;

public class AddTaskFragment extends Fragment implements View.OnClickListener {
    private EditText custName, custPhoneNumber, building, block, appDate, appTime;
    private Button btnAddTask;
    private CoordinatorLayout coordinatorLayout;
    private String[] inputData;
    private WebAppointment webApp;
    private ProgressBar bar;
    private Snackbar snackbar;

    public static final String EMP_ID = "empid";
    private static final String TIME24HOURS_PATTERN = "^([01]?[0-9]|2[0-3]):([0-5][0-9]|[0-9])$";
    private static final String DATE_PATTERN = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";
    private static final String PHONE_PATTERN = "^(\\d){8}$";

    public String appid = "";

    public static AddTaskFragment newInstance(String id) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EMP_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        init(view);
        try {
            webApp = (WebAppointment) this.getArguments().getSerializable("data");
            if (webApp != null) {
                appid = webApp.getAppId();
                custName.setText(webApp.getName());
                custPhoneNumber.setText(webApp.getPhone());
                building.setText(webApp.getBuilding());
                block.setText(webApp.getBlock());
                appDate.setText(webApp.getDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void showDate(View v) {
        DialogFragment newFragment = new DateDialog();
        newFragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
    }

    public void showTime(View v) {
        DialogFragment newFragment = new TimeDialog();
        newFragment.show(getActivity().getSupportFragmentManager(), "TimePicker");
    }

    public void init(View view) {
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.addTaskLayout);
        custName = (EditText) view.findViewById(R.id.custName);
        custPhoneNumber = (EditText) view.findViewById(R.id.custPhone);
        building = (EditText) view.findViewById(R.id.custAddressBuilding);
        block = (EditText) view.findViewById(R.id.custAddressBlock);
        appDate = (EditText) view.findViewById(R.id.appointmentDate);
        appTime = (EditText) view.findViewById(R.id.appointmentTime);
        btnAddTask = (Button) view.findViewById(R.id.btnAddTask);
        bar = (ProgressBar) view.findViewById(R.id.progressBarForAddTask);

        bar.setVisibility(View.INVISIBLE);

        btnAddTask.setOnClickListener(this);
        appDate.setOnClickListener(this);
        appTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.appointmentDate:
                showDate(view);
                break;
            case R.id.appointmentTime:
                showTime(view);
                break;
            case R.id.btnAddTask:

                if (custName.getText().toString().trim().equalsIgnoreCase("") ||
                        custPhoneNumber.getText().toString().trim().equalsIgnoreCase("") ||
                        building.getText().toString().trim().equalsIgnoreCase("") ||
                        block.getText().toString().trim().equalsIgnoreCase("") ||
                        appDate.getText().toString().trim().equalsIgnoreCase("") ||
                        appTime.getText().toString().trim().equalsIgnoreCase("")) {

                    snackbar = Snackbar.make(coordinatorLayout, "All fields are required", Snackbar.LENGTH_LONG);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);


                    snackbar.show();

                } else {
                    if (appDate.getText().toString().matches(DATE_PATTERN) &&
                            appTime.getText().toString().matches(TIME24HOURS_PATTERN)&&
                            custPhoneNumber.getText().toString().matches(PHONE_PATTERN)) {

                        String[] data = {this.getArguments().getString(EMP_ID),
                                custName.getText().toString().trim(),
                                custPhoneNumber.getText().toString().trim(),
                                block.getText().toString().trim(),
                                building.getText().toString().trim(),
                                appDate.getText().toString().concat(" " + appTime.getText().toString().trim()).trim(),
                                appid};
                        inputData = data.clone();

                        new addTask().execute(data);
                    } else {
                        snackbar = Snackbar.make(coordinatorLayout, "Some fields are invalid", Snackbar.LENGTH_LONG);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);

                        snackbar.setAction("RE-INPUT", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!appTime.getText().toString().matches(TIME24HOURS_PATTERN))
                                    appTime.setText("");
                                if (!appDate.getText().toString().matches(DATE_PATTERN))
                                    appDate.setText("");
                                if (!custPhoneNumber.getText().toString().matches(PHONE_PATTERN))
                                    custPhoneNumber.setText("");
                            }
                        });
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                    }


                }


            default:
                break;
        }
    }

    private class addTask extends AsyncTask<String, Integer, String> {
        private Snackbar snackbar;
        private ProgressBar bar;
        int progress_status = 0;

        @Override
        protected void onPreExecute() {
            this.bar = (ProgressBar) getView().findViewById(R.id.progressBarForAddTask);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            // Add appointment
            SyncManager syncManager = new SyncManager("addAppointment.php");

            publishProgress(progress_status);

            return syncManager.syncAppointment(params[0], params[1], params[2], params[3], params[4], params[5], params[6]);
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (this.bar != null)
                bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            bar.setVisibility(View.GONE);

            if (s.equalsIgnoreCase("true")) {
                snackbar = Snackbar.make(coordinatorLayout, "Updated", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);


                snackbar.show();


                if(webApp!=null) {
                    WebAppointmentDAO webAppDAO = new WebAppointmentDAO(getContext());
                    webAppDAO.delete(webApp.getId());
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                fm.popBackStack();
//                ft.commit();

            } else {
                snackbar = Snackbar.make(coordinatorLayout, "Update Failed", Snackbar.LENGTH_LONG);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new addTask().execute(inputData);
                    }
                });

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
            }

        }
    }

}
