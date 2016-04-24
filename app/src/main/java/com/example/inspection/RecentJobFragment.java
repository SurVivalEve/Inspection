package com.example.inspection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.inspection.adapters.RecentJobAdapter;

public class RecentJobFragment extends Fragment {
    public static final String ARGS_PAGE = "args_page";

    public static RecentJobFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE, page);
        RecentJobFragment fragment = new RecentJobFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_job,container,false);

        //Fragment+ViewPager+FragmentViewPager
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_recent_job);

        RecentJobAdapter adapter = new RecentJobAdapter(getChildFragmentManager(),getContext());
        viewPager.setAdapter(adapter);

        //TabLayout
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabLayout_recent_job);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


}
