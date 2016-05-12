package com.example.inspection;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.adapters.AppointmentAdapter;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.WebAppointment;
import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.Schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentFragment extends Fragment {

    private RecyclerView rv;
    private CoordinatorLayout coordinatorLayout;
    private List<WebAppointment> webAppointments;

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
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        rv = (RecyclerView) view.findViewById(R.id.rv);

        String empid = getArguments().getString(EMP_ID);

        WebAppointmentDAO webAppDAO = new WebAppointmentDAO(getContext());
        webAppointments = webAppDAO.getAll();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new AppointmentAdapter(getContext(), webAppointments));

        return view;
    }



}
