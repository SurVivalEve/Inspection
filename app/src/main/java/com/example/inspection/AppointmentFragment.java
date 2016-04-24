package com.example.inspection;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.models.Appointment;
import com.example.inspection.models.Customer;
import com.example.inspection.models.Schedule;

import java.util.Calendar;
import java.util.Date;

public class AppointmentFragment extends Fragment {

    private String empID="";
    private String[] headers = {"", "Time", "Address"};
    private LayoutInflater layoutInflater;
    private LinearLayout dataCol;
    private View[] item_appointment_header = new View[headers.length];

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        empID = getArguments().getString("empID");

        dataCol = (LinearLayout) view.findViewById(R.id.dataCol);

        layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        addData();

        //TODO: on item click listeners
        return view;
    }

    private void addData(){
        Schedule aptList = new Schedule();
        Customer cust = new Customer(
                "C00000000002",
                "Fong Tak Wah",
                "96230972",
                Customer.Sex.valueOf("M"),
                ""//customer email
        );
        Appointment[] apts = new Appointment[6];
        for(int i=0; i<6; i++) {
            apts[i] = new Appointment(
                    "A00000000030",
                    "Processing",
                    "",
                    "",
                    "5",
                    "Des Voeux Road Central, Hong Kong Island",
                    "Wan Chai District",
                    new Date(),
                    empID,
                    cust
            );
        }
        aptList.getAppointments().add(apts[0]);
        apts[1].setBuilding("Two International Finance Centre, 8 Finance Street Central ");
        aptList.getAppointments().add(apts[1]);
        apts[2].setBuilding("Chinachem Exchange Square, 1 Hoi Wan Street, Quarry Bay");
        aptList.getAppointments().add(apts[2]);
        apts[3].setBuilding("Hong Kong Branch, 20 Pedder Street, Central, Hong Kong");
        aptList.getAppointments().add(apts[3]);
        apts[4].setBuilding("Wong Tai Sin District");
        aptList.getAppointments().add(apts[4]);
        apts[5].setBuilding("Pico Tower");
        aptList.getAppointments().add(apts[5]);
        View[] item_appointment_data_view = new View[6];
        for(int i=0; i<6; i++) {
            item_appointment_data_view[i] = layoutInflater.inflate(R.layout.item_appointment_data_view, null);
            TextView col1 = (TextView) item_appointment_data_view[i].findViewById(R.id.col1);
            TextView col2 = (TextView) item_appointment_data_view[i].findViewById(R.id.col2);
            TextView col3 = (TextView) item_appointment_data_view[i].findViewById(R.id.col3);
            col1.setText((i + 1) + "");
            if(Math.random()<0.33)
                col2.setText("25-Apr-2016\nMorning");
            else if(Math.random()<0.66)
                col2.setText("25-Apr-2016\nAfternoon");
            else
                col2.setText("25-Apr-2016\nEvening");
            col3.setText(aptList.getAppointments().get(i).getBuilding());
            final int index = i;
            final Appointment apt = aptList.getAppointments().get(index);
            item_appointment_data_view[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppointmentDetailFragment appointmentDetailFragment = new AppointmentDetailFragment();
                    FragmentTransaction ft = ((MainMenu) getContext()).getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("appointment", apt);
                    appointmentDetailFragment.setArguments(bundle);
                    ft.replace(R.id.main_fragment, appointmentDetailFragment, "appointment_detail_fragment")
                            .addToBackStack(null)
                            .commit();
                }
            });
            dataCol.addView(item_appointment_data_view[i]);
            View lineSepartor = layoutInflater.inflate(R.layout.item_horizontal_line, null);
            dataCol.addView(lineSepartor);
        }
    }
}
