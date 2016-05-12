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
import com.example.inspection.dbmodels.LocalRecentJob;
import com.example.inspection.models.Processing;
import com.example.inspection.models.RecentJob;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sur.Vival on 13/3/2016.
 */
public class RecentJobListProcessingAdapter extends RecyclerView.Adapter<RecentJobListProcessingAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<LocalRecentJob> processings;


    public RecentJobListProcessingAdapter(Context context, List<LocalRecentJob> processings) {
        this.context = context;
        this.processings = processings;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_recentjob, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LocalRecentJob p = processings.get(position);
        holder.empName.setText("Alex");
        holder.custName.setText(p.getFullname());
        holder.custAddress.setText(p.getBuilding());
        holder.custPhone.setText(p.getPhone());
        holder.setProcessing(p);

    }

    @Override
    public int getItemCount() {
        return processings == null ? 0 : processings.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        //reduce findViewById
        public TextView custName, custPhone, custAddress, empName;
        public CircleImageView custSexImage;
        public LocalRecentJob processing;

        public ViewHolder(View itemView) {
            super(itemView);
            empName = (TextView) itemView.findViewById(R.id.recentjob_empName);
            custName = (TextView) itemView.findViewById(R.id.recentjob_custName);
            custPhone = (TextView) itemView.findViewById(R.id.recentjob_custPhone);
            custAddress = (TextView) itemView.findViewById(R.id.recentjob_custAddress);
            custSexImage = (CircleImageView) itemView.findViewById(R.id.recentjob_sexImage);
            itemView.setOnClickListener(itemViewClicked);
        }

        public LocalRecentJob getCustomer() {
            return processing;
        }

        public void setProcessing(LocalRecentJob processing) {
            this.processing = processing;
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
                bundle.putSerializable("processing",processing);
                jobDetailFragment.setArguments(bundle);
                ft.replace(R.id.main_fragment,jobDetailFragment,"job_details_fragment")
                        .addToBackStack(null)
                        .commit();

            }
        };
    }


}
