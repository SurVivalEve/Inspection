package com.example.inspection;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.adapters.AppointmentAdapter;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.WebAppointment;
import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.Schedule;
import com.example.inspection.sync.SyncManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentFragment extends Fragment {

    private RecyclerView rv;
    private CoordinatorLayout coordinatorLayout;
    private List<WebAppointment> webAppointments;
    private SwipeRefreshLayout laySwip;
    WebAppointmentDAO webAppDAO;
    private Snackbar snackbar;

    public static final String EMP_ID = "empid";

    public static AppointmentFragment newInstance(String id) {
        AppointmentFragment fragment = new AppointmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EMP_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_appointment, container, false);
        rv = (RecyclerView) view.findViewById(R.id.rvAppointment);
        laySwip = (SwipeRefreshLayout) view.findViewById(R.id.laySwipeAppointment);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.webAppointment_layout);

        String empid = getArguments().getString(EMP_ID);

        webAppDAO = new WebAppointmentDAO(getContext());
        webAppointments = webAppDAO.getAll();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new AppointmentAdapter(getContext(), webAppointments));

        init(view);

        return view;
    }

    private void init(View view) {
        laySwip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                laySwip.setRefreshing(true);

                new getExistAppointment().execute();

                snackbar = Snackbar.make(coordinatorLayout, "Updating", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);

                snackbar.show();

            }
        });
    }

    private class getExistAppointment extends AsyncTask<String, Integer, String> {
        private Snackbar snackbar;

        @Override
        protected String doInBackground(String... params) {
            SyncManager syncManager = new SyncManager("watchAppointment.php");

            return syncManager.syncExistsAppointment(getContext());
        }


        @Override
        protected void onPostExecute(String s) {
            laySwip.setRefreshing(false);
            if (s.equalsIgnoreCase("true")) {
                webAppointments = webAppDAO.getAll();
                rv.setAdapter(new AppointmentAdapter(getContext(), webAppointments));

            } else if (s.equalsIgnoreCase("empty")) {
                snackbar = Snackbar.make(coordinatorLayout, "No Record Find", Snackbar.LENGTH_LONG);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new getExistAppointment().execute();
                    }
                });

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
            } else {
                snackbar = Snackbar.make(coordinatorLayout, "Update Failed", Snackbar.LENGTH_LONG);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new getExistAppointment().execute();
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
