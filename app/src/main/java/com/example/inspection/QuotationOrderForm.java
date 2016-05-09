package com.example.inspection;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QuotationOrderForm extends Fragment {

    private LinearLayout col1, col4;
    private EditText col2_1, col2_2, col3_1, constructionFee, earnest, owe, remark;
    private ArrayList<View> col1_View = new ArrayList<>();
    private ArrayList<EditText[]> col1_ETs = new ArrayList<>();
    private ArrayList<Button> col1_dels = new ArrayList<>();
    private ArrayList<View> col4_View = new ArrayList<>();
    private ArrayList<EditText> col4_ETs = new ArrayList<>();
    private ArrayList<Button> col4_dels = new ArrayList<>();
    private Button col1_plus, col4_plus, save;
    private LayoutInflater layoutInflater;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotation_order_form, container , false);
        init(view);

        layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setAllOnClickListener();

        return view;
    }

    public void init(View view){
        col1 = (LinearLayout) view.findViewById(R.id.col1);
        col4 = (LinearLayout) view.findViewById(R.id.col4);
        col2_1 = (EditText) view.findViewById(R.id.col2_1);
        col2_2 = (EditText) view.findViewById(R.id.col2_2);
        col3_1 = (EditText) view.findViewById(R.id.col3_1);
        constructionFee = (EditText) view.findViewById(R.id.constructionFee);
        earnest = (EditText) view.findViewById(R.id.earnest);
        owe = (EditText) view.findViewById(R.id.owe);
        remark = (EditText) view.findViewById(R.id.remark);
        col1_plus = (Button) view.findViewById(R.id.col1_plus);
        col4_plus = (Button) view.findViewById(R.id.col4_plus);
        save = (Button) view.findViewById(R.id.save);
    }

    public void createView(){
        col1_plus.performClick();
    }

    public void setAllOnClickListener(){
        col1_plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                View v = layoutInflater.inflate(R.layout.item_quotation_orderform_col1, null);
                col1_View.add(v);

                EditText[] col1_ET = new EditText[3];
                col1_ET[0] = (EditText) v.findViewById(R.id.col1_1);
                col1_ET[1] = (EditText) v.findViewById(R.id.col1_2);
                col1_ET[2] = (EditText) v.findViewById(R.id.col1_3);
                col1_ETs.add(col1_ET);

                Button col1_del = (Button) v.findViewById(R.id.del);
                final int currentIndex = col1_View.indexOf(v);
                col1_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        col1_View.remove(currentIndex);
                        col1_ETs.remove(currentIndex);
                        col1_dels.remove(currentIndex);
                        col1.removeViewAt(currentIndex);
                    }
                });
                col1_dels.add(col1_del);

                col1.addView(col1_View.get(col1_View.size() - 1));
            }
        });
        col4_plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                View v = layoutInflater.inflate(R.layout.item_quotation_orderform_col4, null);
                col4_View.add(v);

                EditText col4_ET = (EditText) v.findViewById(R.id.col4_1);
                col4_ETs.add(col4_ET);

                Button col4_del = (Button) v.findViewById(R.id.del);
                final int currentIndex = col4_View.indexOf(v);
                col4_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        col4_View.remove(currentIndex);
                        col4_ETs.remove(currentIndex);
                        col4_dels.remove(currentIndex);
                        col4.removeViewAt(currentIndex);
                    }
                });
                col4_dels.add(col4_del);

                col4.addView(col4_View.get(col4_View.size() - 1));
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
