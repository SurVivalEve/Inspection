package com.example.inspection;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspection.util.FileWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

    private QuotationsListener mCallback;

    public interface QuotationsListener {
        public void sendOrderFormMessage(JSONArray jsonArray);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (QuotationsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement QuotationsListener");
        }

    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

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
                String error_message = "";
                error_message = checkInputFormat();
                if (error_message == "") {
                    JSONArray orderForm = new JSONArray();

                    try {
                        JSONArray hinge = new JSONArray();

                        for (int i = 0; i < col1_View.size(); i++) {
                            JSONObject hinge_data = new JSONObject();
                            JSONArray hinge_data1s = new JSONArray();
                            for (int k = 0; k < 3; k++) {
                                JSONObject hinge_data1_data = new JSONObject();
                                hinge_data1_data.put("data" + (k + 1), col1_ETs.get(i)[k].getText().toString().replace(" ", ""));
                                hinge_data1s.put(hinge_data1_data);
                            }
                            hinge_data.put("data", hinge_data1s);
                            hinge.put(hinge_data);
                        }

                        JSONObject col1 = new JSONObject();
                        if (col1_View.size() == 0) {
                            col1.put("hinge", JSONObject.NULL);
                        } else {
                            col1.put("hinge", hinge);
                        }
                        orderForm.put(col1);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get col1 data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //col2
                    try {
                        JSONArray switchJsona = new JSONArray();

                        JSONObject switch_data1 = new JSONObject();
                        if (col2_1.getText().toString().isEmpty()) {
                            switch_data1.put("data1", JSONObject.NULL);
                        } else {
                            switch_data1.put("data1", col2_1.getText().toString().replace(" ", ""));
                        }
                        switchJsona.put(switch_data1);

                        JSONObject switch_data2 = new JSONObject();
                        if (col2_2.getText().toString().isEmpty()) {
                            switch_data2.put("data2", JSONObject.NULL);
                        } else {
                            switch_data2.put("data2", col2_2.getText().toString().replace(" ", ""));
                        }
                        switchJsona.put(switch_data2);

                        JSONObject switchJson = new JSONObject();
                        switchJson.put("switch", switchJsona);
                        orderForm.put(switchJson);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get col2 data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //col3 data
                    try {
                        JSONObject col3 = new JSONObject();
                        if (col3_1.getText().toString().isEmpty()) {
                            col3.put("strip", JSONObject.NULL);
                        } else {
                            col3.put("strip", col3_1.getText().toString().replace(" ", ""));
                        }
                        orderForm.put(col3);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get col3 data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //col4 data
                    try {
                        JSONArray others = new JSONArray();
                        int index = 0;
                        for (int i = 0; i < col4_View.size(); i++) {
                            if (!col4_ETs.get(i).getText().toString().isEmpty()) {
                                JSONObject hinge_data = new JSONObject();
                                hinge_data.put("data" + (++index), col4_ETs.get(i).getText().toString());
                                others.put(hinge_data);
                            }
                        }
                        JSONObject col4 = new JSONObject();
                        if (index == 0) {
                            col4.put("others", JSONObject.NULL);
                        } else {
                            col4.put("others", others);
                        }
                        orderForm.put(col4);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get col4 data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //construction fee data
                    try {
                        JSONObject constructionfee = new JSONObject();
                        constructionfee.put("constructionfee", constructionFee.getText().toString().replace(" ", ""));
                        orderForm.put(constructionfee);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get construction fee data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //earnest data
                    try {
                        JSONObject earnestmoney = new JSONObject();
                        earnestmoney.put("earnestmoney", earnest.getText().toString().replace(" ", ""));
                        orderForm.put(earnestmoney);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get earnest data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //owe data
                    try {
                        JSONObject owemoney = new JSONObject();
                        owemoney.put("owemoney", owe.getText().toString().replace(" ", ""));
                        orderForm.put(owemoney);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get owe data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }
                    //remark data
                    try {
                        JSONObject remarkJson = new JSONObject();
                        if (remark.getText().toString().isEmpty()) {
                            remarkJson.put("remark", JSONObject.NULL);
                        } else {
                            remarkJson.put("remark", remark.getText().toString());
                        }
                        orderForm.put(remarkJson);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get remark data json exception");
                        Log.d("detail", e.toString());
                        if(error_message == "")
                            error_message = "Occur exception when generate json";
                    }

                    Log.d("order form json", orderForm.toString());

                    if(error_message == "") {
                        mCallback.sendOrderFormMessage(orderForm);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(fm.findFragmentByTag("quotationOrderForm")).commit();
                        fm.popBackStack();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String checkInputFormat(){
        String error_message = "";
        if(!isNumeric(owe.getText().toString()) || !isNumeric(earnest.getText().toString()) || !isNumeric(constructionFee.getText().toString())) {
            error_message = "Amount value only can be type in numeric with no more 2 dec. places!";
        }
        if(owe.getText().toString().replace(" ", "").isEmpty()
                || earnest.getText().toString().replace(" ", "").isEmpty()
                || constructionFee.getText().toString().replace(" ", "").isEmpty()) {
            error_message = "All amount value cannot be empty";
        }
        if(col4_View.size()!=0){
            for(int i=0; i<col4_View.size(); i++){
                if (col4_ETs.get(i).getText().toString().replace(" ", "").isEmpty())
                    error_message = "each value on additional column in others cannot be empty";
            }
        }
        if(col1_View.size()!=0){
            for(int i=0; i<col1_View.size(); i++){
                for(int k=0; k<3; k++) {
                    //if (col1_ETs.get(i)[k].getText().toString().replace(" ", "").matches("^\\d+$"))

                    if (col1_ETs.get(i)[k].getText().toString().replace(" ", "").isEmpty())
                        error_message = "each value on additional column in changing hinge construction cannot be empty";
                }
            }
        }
        return error_message;
    }

    public static boolean isNumeric(String str)
    {
        return str.replace(" ", "").matches("^\\d+(\\.\\d\\d)?$") || str.replace(" ", "").matches("^\\d+(\\.\\d)?$");  //match a number with no more then 2 decimal place
    }
}
