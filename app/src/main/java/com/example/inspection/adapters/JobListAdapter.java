package com.example.inspection.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inspection.JobDetailFragment;
import com.example.inspection.MainMenu;
import com.example.inspection.R;
import com.example.inspection.models.Appointment;
import com.example.inspection.models.Schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sur.Vival on 15/3/2016.
 */
public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Appointment> appointments;

    public JobListAdapter(Context context, Schedule s) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.appointments = s.getAppointments();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_recentjob, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Appointment a = appointments.get(position);
        holder.empName.setText(a.getEmpName());
        holder.custName.setText(a.getCustomer().getFullname());
        holder.custPhone.setText(a.getCustomer().getPhone());
        holder.custAddress.setText(a.getBuilding().concat(" "+a.getFlatBlock()));
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd\nHH:mm");
        holder.apptime.setText(formatter.format(a.getDate()));
        holder.setAppointment(a);
//        holder.apptime.setText(a.getDate().getYear()+1900+"-"+a.getDate().getMonth()+1+"-"+a.getDate().getDate()+
//                "\n"+a.getDate().getHours()+":"+a.getDate().getMinutes());
    }

    @Override
    public int getItemCount() {
        return appointments == null ? 0 : appointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //reduce findViewById
        public TextView custName, custPhone, custAddress, empName, apptime;
        public CircleImageView custSexImage;
        public Appointment appointment;

        public ViewHolder(View itemView) {
            super(itemView);
            empName = (TextView) itemView.findViewById(R.id.recentjob_empName);
            custName = (TextView) itemView.findViewById(R.id.recentjob_custName);
            custPhone = (TextView) itemView.findViewById(R.id.recentjob_custPhone);
            custAddress = (TextView) itemView.findViewById(R.id.recentjob_custAddress);
            custSexImage = (CircleImageView) itemView.findViewById(R.id.recentjob_sexImage);
            apptime = (TextView) itemView.findViewById(R.id.recentjob_apptime);
            itemView.setOnClickListener(itemViewClicked);
        }

        public Appointment getAppointment() {
            return appointment;
        }

        public void setAppointment(Appointment appointment) {
            this.appointment = appointment;
        }

        private final View.OnClickListener itemViewClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //For fragment

//                CustomerDetailFragment customerDetailFragment = new CustomerDetailFragment();
//                FragmentTransaction ft = ((MainMenu)context).getSupportFragmentManager().beginTransaction();
//                Bundle bundle = new Bundle();
//                bundle.putString("custid", customer.getId());
//                customerDetailFragment.setArguments(bundle);
//                ft.replace(R.id.main_fragment, customerDetailFragment)
//                        .addToBackStack(null)
//                        .commit();

                JobDetailFragment jobDetailFragment = new JobDetailFragment();
                FragmentTransaction ft = ((MainMenu)context).getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("appointment",appointment);
                jobDetailFragment.setArguments(bundle);
                ft.replace(R.id.main_fragment,jobDetailFragment,"job_details_fragment")
                        .addToBackStack(null)
                        .commit();
            }
        };
    }
}
