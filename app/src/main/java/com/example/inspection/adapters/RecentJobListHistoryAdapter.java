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
import com.example.inspection.models.History;
import com.example.inspection.models.RecentJob;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sur.Vival on 13/3/2016.
 */
public class RecentJobListHistoryAdapter extends RecyclerView.Adapter<RecentJobListHistoryAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<History> histories;


    public RecentJobListHistoryAdapter(Context context, RecentJob recentJob) {
        this.context = context;
        this.histories = recentJob.getHistories();
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_recentjob, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        History p = histories.get(position);
        holder.empName.setText("Alex");
        holder.custName.setText(p.getFullname());
        holder.custAddress.setText(p.getBuilding());
        holder.custPhone.setText(p.getPhone());
        holder.setHistory(p);

    }

    @Override
    public int getItemCount() {
        return histories == null ? 0 : histories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //reduce findViewById
        public TextView custName, custPhone, custAddress, empName;
        public CircleImageView custSexImage;
        public History history;

        public ViewHolder(View itemView) {
            super(itemView);
            empName = (TextView) itemView.findViewById(R.id.recentjob_empName);
            custName = (TextView) itemView.findViewById(R.id.recentjob_custName);
            custPhone = (TextView) itemView.findViewById(R.id.recentjob_custPhone);
            custAddress = (TextView) itemView.findViewById(R.id.recentjob_custAddress);
            custSexImage = (CircleImageView) itemView.findViewById(R.id.recentjob_sexImage);
            itemView.setOnClickListener(itemViewClicked);
        }

        public History getHistory() {
            return history;
        }

        public void setHistory(History history) {
            this.history = history;
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
                bundle.putSerializable("history",history);
                jobDetailFragment.setArguments(bundle);
                ft.replace(R.id.main_fragment,jobDetailFragment,"job_details_fragment")
                        .addToBackStack(null)
                        .commit();

            }
        };
    }

}
