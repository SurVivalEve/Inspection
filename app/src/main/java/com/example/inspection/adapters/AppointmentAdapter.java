package com.example.inspection.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.inspection.AddTaskFragment;
import com.example.inspection.MainMenu;
import com.example.inspection.R;
import com.example.inspection.dao.WebAppointmentDAO;
import com.example.inspection.dbmodels.WebAppointment;

import java.util.List;

/**
 * Created by Sur.Vival on 11/5/2016.
 */
public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<WebAppointment> webAppointments;

    public AppointmentAdapter(Context context, List<WebAppointment> webAppointments) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.webAppointments = webAppointments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_appointment, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WebAppointment webApp = webAppointments.get(position);
        holder.custName.setText(webApp.getName());
        holder.custAddress.setText(webApp.getBuilding()+" "+webApp.getBlock());
        holder.custPhone.setText(webApp.getPhone());
        holder.custDate.setText(webApp.getDate());
        holder.custRemark.setText(webApp.getRemark());
        holder.setWebApp(webApp);
    }

    @Override
    public int getItemCount() {
        return webAppointments == null ? 0 : webAppointments.size();
    }

    public void delete(int position) {
        webAppointments.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView custName, custPhone, custAddress, custDate, custRemark;
        public Button btnAccept, btnCancel;
        public WebAppointment webApp;
        public WebAppointmentDAO webAppDAO = new WebAppointmentDAO(context);

        public ViewHolder(View itemView) {
            super(itemView);
            btnAccept = (Button) itemView.findViewById(R.id.btnAcceptApp);
            btnCancel = (Button) itemView.findViewById(R.id.btnCancelApp);
            custName = (TextView) itemView.findViewById(R.id.webCustName);
            custPhone = (TextView) itemView.findViewById(R.id.webCustPhone);
            custAddress = (TextView) itemView.findViewById(R.id.webCustAddress);
            custDate = (TextView) itemView.findViewById(R.id.webCustDate);
            custRemark = (TextView) itemView.findViewById(R.id.webCustRemark);

            itemView.setOnClickListener(itemViewClicked);
            btnAccept.setOnClickListener(accept);
            btnCancel.setOnClickListener(cancel);
        }

        public WebAppointment getWebApp() {
            return webApp;
        }

        public void setWebApp(WebAppointment webApp) {
            this.webApp = webApp;
        }

        private final View.OnClickListener itemViewClicked = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        };

        private final View.OnClickListener accept = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AddTaskFragment addTaskFragment = new AddTaskFragment();
                FragmentTransaction ft = ((MainMenu)context).getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data",getWebApp());
                addTaskFragment.setArguments(bundle);
                ft.replace(R.id.main_fragment,addTaskFragment,"addTaskByClient")
                        .addToBackStack(null)
                        .commit();
                webAppDAO.delete(getWebApp().getId());
            }
        };

        private final View.OnClickListener cancel = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                delete(getAdapterPosition());
                webAppDAO.delete(getWebApp().getId());
            }
        };

    }
}
