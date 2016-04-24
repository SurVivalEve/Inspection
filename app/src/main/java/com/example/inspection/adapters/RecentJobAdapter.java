package com.example.inspection.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.inspection.RecentJobListFragment;

public class RecentJobAdapter extends FragmentPagerAdapter {
    public final int COUNT = 2;
    private String[] titles = new String[]{"Processing", "History"};
    private Context context;

    public RecentJobAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RecentJobListFragment processingFragment = new RecentJobListFragment();
                return processingFragment.newInstance("Processing");
            case 1:
                RecentJobListFragment historyFragment = new RecentJobListFragment();
                return historyFragment.newInstance("History");
        }
        return null;

    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
