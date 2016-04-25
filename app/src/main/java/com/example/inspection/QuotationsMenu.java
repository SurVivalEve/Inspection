package com.example.inspection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sur.Vival on 25/4/2016.
 */
public class QuotationsMenu extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotations_menu, container , false);

        init();

        return view;
    }

    public void init() {

    }
}
