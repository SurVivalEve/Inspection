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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class QuotationOrderForm extends Fragment {

    private LinearLayout col1, col4;
    private EditText col2_1, col2_2, col3_1, constructionFee, earnest, owe, remark;
    private ArrayList<View> col1_View = new ArrayList<>();
    private ArrayList<TextView> col1_TVs = new ArrayList<>();
    private ArrayList<EditText[]> col1_ETs = new ArrayList<>();
    private ArrayList<ImageButton> col1_dels = new ArrayList<>();
    private ArrayList<View> col4_View = new ArrayList<>();
    private ArrayList<TextView> col4_TVs = new ArrayList<>();
    private ArrayList<EditText> col4_ETs = new ArrayList<>();
    private ArrayList<ImageButton> col4_dels = new ArrayList<>();
    private ImageButton col1_plus, col4_plus;
    private Button save;
    private LayoutInflater layoutInflater;
    private int col1Index=0, col4Index=0;

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
        col1_plus = (ImageButton) view.findViewById(R.id.col1_plus);
        col4_plus = (ImageButton) view.findViewById(R.id.col4_plus);
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

                TextView col1_TV = (TextView) v.findViewById(R.id.hidden_index);
                final String index = String.valueOf(col1Index++);
                col1_TV.setText(index);
                col1_TVs.add(col1_TV);

                EditText[] col1_ET = new EditText[3];
                col1_ET[0] = (EditText) v.findViewById(R.id.col1_1);
                col1_ET[1] = (EditText) v.findViewById(R.id.col1_2);
                col1_ET[2] = (EditText) v.findViewById(R.id.col1_3);
                col1_ETs.add(col1_ET);

                ImageButton col1_del = (ImageButton) v.findViewById(R.id.del);
                col1_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int i=0; i<col1Index; i++) {
                            if(col1_TVs.get(i).getText().toString().equalsIgnoreCase(index)) {
                                col1_View.remove(i);
                                col1_TVs.remove(i);
                                col1_ETs.remove(i);
                                col1_dels.remove(i);
                                col1.removeViewAt(i);
                                break;
                            }
                        }
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

                TextView col4_TV = (TextView) v.findViewById(R.id.hidden_index);
                final String index = String.valueOf(col4Index++);
                col4_TV.setText(index);
                col4_TVs.add(col4_TV);

                EditText col4_ET = (EditText) v.findViewById(R.id.col4_1);
                col4_ETs.add(col4_ET);

                ImageButton col4_del = (ImageButton) v.findViewById(R.id.del);
                col4_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    for(int i=0; i<col4Index; i++) {
                        if(col4_TVs.get(i).getText().toString().equalsIgnoreCase(index)) {
                            col4_View.remove(i);
                            col4_TVs.remove(i);
                            col4_ETs.remove(i);
                            col4_dels.remove(i);
                            col4.removeViewAt(i);
                            break;
                        }
                    }
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
