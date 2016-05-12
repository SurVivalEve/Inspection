package com.example.inspection;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.inspection.adapters.RecentJobAdapter;
import com.example.inspection.sync.SyncManager;

public class RecentJobFragment extends Fragment {
    public static final String PAGE = "page";
    public static final String EMP_ID = "empid";

    private SwipeRefreshLayout laySwipeRecentJob;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CoordinatorLayout coordinatorLayout;

    public static RecentJobFragment newInstance(int page, String id) {
        RecentJobFragment fragment = new RecentJobFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE, page);
        bundle.putString(EMP_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_job,container,false);

        //Fragment+ViewPager+FragmentViewPager
        viewPager = (ViewPager) view.findViewById(R.id.viewpager_recent_job);

        RecentJobAdapter adapter = new RecentJobAdapter(getChildFragmentManager(),getContext());

        viewPager.setAdapter(adapter);

        //TabLayout
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_recent_job);
        tabLayout.setupWithViewPager(viewPager);

        init(view);

        return view;
    }

    private void init(View view) {
        laySwipeRecentJob = (SwipeRefreshLayout) view.findViewById(R.id.laySwipeRecentJob);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.recentjob_layout);

        laySwipeRecentJob.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                laySwipeRecentJob.setRefreshing(true);

                new GetPersonalSchedule().execute(String.valueOf(getArguments().get(EMP_ID)));

                Toast.makeText(getContext(), "Updating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetPersonalSchedule extends AsyncTask<String,Void,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            SyncManager syncManager = new SyncManager("getRecentRecord.php?empID="+params[0]);

            return syncManager.syncRecentJob(getContext());
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            laySwipeRecentJob.setRefreshing(false);

            if (!aBoolean) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "No recent job record", Snackbar.LENGTH_LONG);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);

                snackbar.show();
            }

            RecentJobAdapter adapter = new RecentJobAdapter(getChildFragmentManager(),getContext());

            viewPager.setAdapter(adapter);

        }
    }


}
