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
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.inspection.adapters.CustomerListAdapter;
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

    public static final String EMP_ID = "empid";

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
                String[] data = {this.getArguments().getString(EMP_ID),
                        custName.getText().toString(),
                        custPhoneNumber.getText().toString(),
                        block.getText().toString(),
                        building.getText().toString(),
                        appDate.getText().toString().concat(" " + appTime.getText().toString())};
                inputData = data.clone();
                new addTask().execute(data);
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
            this.bar = (ProgressBar) getView().findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            // Add appointment
            SyncManager syncManager = new SyncManager("addAppointment.php");

            publishProgress(progress_status);
            SystemClock.sleep(1000);
            return syncManager.syncAppointment(params[0], params[1], params[2], params[3], params[4], params[5]);
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
