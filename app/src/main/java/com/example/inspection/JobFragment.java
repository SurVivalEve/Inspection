package com.example.inspection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inspection.adapters.JobListAdapter;
import com.example.inspection.models.Schedule;

/**
 * Created by Sur.Vival on 16/3/2016.
 */
public class JobFragment extends Fragment {

    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        Bundle bundle = this.getArguments();
        Schedule schedule = (Schedule) bundle.getSerializable("joblist");
        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new JobListAdapter(getContext(),schedule));
        return view;
    }


}
