package com.example.inspection;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.inspection.Dialog.SignpadDialog;
import com.example.inspection.util.FileWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

public class Setting extends Fragment {

    private Button switchLanguage, logout;
    private String empID = "", empName = "";

    public static Setting newInstance(String id, String name) {
        Setting fragment = new Setting();
        Bundle bundle = new Bundle();
        bundle.putString("empID", id);
        bundle.putString("empName", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container , false);
        init(view);

        //set all button onclick listener
        setAllOnClickListener();

        return view;
    }

    public void init(View view){
        empID = getArguments().getString("empID");
        empName = getArguments().getString("empName");

        switchLanguage = (Button) view.findViewById(R.id.switchLanguage);
        logout = (Button) view.findViewById(R.id.logout);
    }

    public void setAllOnClickListener(){
        switchLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Resources resources = getActivity().getResources();
                Configuration config = resources.getConfiguration();
                if(config.locale == Locale.ENGLISH){
                    switchLanguage("zh");
                } else if (config.locale == Locale.TRADITIONAL_CHINESE || config.locale.toString().equalsIgnoreCase("zh_hk")){
                    switchLanguage("en");
                }
                getActivity().finish();
                Intent i = new Intent(getActivity(), MainMenu.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("empID", empID);
                i.putExtra("empName", empName);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getActivity().finish();
                startActivity(i);
            }
        });
    }

    public void switchLanguage(String language) {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else if (language.equals("zh")) {
            config.locale = Locale.TRADITIONAL_CHINESE;
        }
        resources.updateConfiguration(config, dm);

        PreferenceUtil.commitString("language", language);
    }
}
