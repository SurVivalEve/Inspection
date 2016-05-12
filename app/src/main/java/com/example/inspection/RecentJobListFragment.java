package com.example.inspection;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inspection.adapters.RecentJobListHistoryAdapter;
import com.example.inspection.adapters.RecentJobListProcessingAdapter;
import com.example.inspection.dao.RecentJobDAO;
import com.example.inspection.dbmodels.LocalRecentJob;
import com.example.inspection.models.RecentJob;
import com.example.inspection.sync.SyncManager;

import java.util.List;

/**
 * Created by Sur.Vival on 13/3/2016.
 */
public class RecentJobListFragment extends Fragment {

    private RecyclerView rv;
    private CoordinatorLayout coordinatorLayout;
    private int postion;
    private String empid;

    public static RecentJobListFragment newInstance(String page) {
        Bundle filter = new Bundle();
        filter.putString("filter", page);
        RecentJobListFragment fragment = new RecentJobListFragment();
        fragment.setArguments(filter);
        return fragment;
    }

    public void setPostion(int postion){
        this.postion = postion;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview,container,false);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.recentjob_layout);

        empid = ((MainMenu)getActivity()).getEmpID();
        // Hard Code Emp id
        Snackbar snackbar;
        RecentJobDAO dao = new RecentJobDAO(getContext());
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<LocalRecentJob> processings = dao.getAllByWhich("processing");
        List<LocalRecentJob> historys = dao.getAllByWhich("history");

        if (getArguments().getString("filter").equals("Processing")) {

            if (processings != null) {
                rv.setAdapter(new RecentJobListProcessingAdapter(getContext(), processings));
            } else {
                snackbar = Snackbar.make(coordinatorLayout, "NO PROCESSING RECORD", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
            }

        } else if (getArguments().getString("filter").equals("History")) {

            if (historys != null) {
                rv.setAdapter(new RecentJobListHistoryAdapter(getContext(), historys));
            } else {
                snackbar = Snackbar.make(coordinatorLayout, "NO HISOTRY RECORD", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
            }

        }


        return view;
    }


    private class GetPersonalSchedule extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            SyncManager syncManager = new SyncManager("getRecentRecord.php?empID="+strings[0]);

            return syncManager.syncRecentJob(getContext());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Snackbar snackbar;

            RecentJobDAO dao = new RecentJobDAO(getContext());
            rv.setLayoutManager(new LinearLayoutManager(getContext()));

            List<LocalRecentJob> processings = dao.getAllByWhich("processing");
            List<LocalRecentJob> historys = dao.getAllByWhich("history");

            if (!aBoolean) {
                snackbar = Snackbar.make(coordinatorLayout,"No recent job record",Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
            } else {

                if (getArguments().getString("filter").equals("Processing")) {

                    if (processings != null) {
                        rv.setAdapter(new RecentJobListProcessingAdapter(getContext(), processings));
                    } else {
                        snackbar = Snackbar.make(coordinatorLayout, "NO PROCESSING RECORD", Snackbar.LENGTH_LONG);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                    }

                } else if (getArguments().getString("filter").equals("History")) {

                    if (historys != null) {
                        rv.setAdapter(new RecentJobListHistoryAdapter(getContext(), historys));
                    } else {
                        snackbar = Snackbar.make(coordinatorLayout, "NO HISOTRY RECORD", Snackbar.LENGTH_LONG);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.YELLOW);
                    }

                }
            }
        }
    }


}
