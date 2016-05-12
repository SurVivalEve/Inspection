package com.example.inspection;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.inspection.util.FileWrapper;
import com.github.gcacace.signaturepad.views.SignaturePad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class QuotationInvoice extends Fragment {

    private EditText remark, amount;
    private ImageView custSign, empSign;
    private Button save;

    private QuotationsListener mCallback;

    public interface QuotationsListener {
        public void sendInvoicMessage(JSONArray jsonArray);
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
        View view = inflater.inflate(R.layout.fragment_quotation_invoice, container , false);
        init(view);

        //set all button onclick listener
        setAllOnClickListener();

        return view;
    }

    public void init(View view){
        remark = (EditText) view.findViewById(R.id.remark);
        amount = (EditText) view.findViewById(R.id.amount);
        custSign = (ImageView) view.findViewById(R.id.custSign);
        empSign = (ImageView) view.findViewById(R.id.empSign);
        save = (Button) view.findViewById(R.id.save);
    }

    public void setAllOnClickListener(){
        custSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Event triggered when the pad is signed
                SignpadDialog signpadDialog = new SignpadDialog();
                Bundle args = new Bundle();
                args.putInt("title", 0);
                signpadDialog.setArguments(args);
                signpadDialog.show(getActivity().getSupportFragmentManager(), "custSign");
            }
        });
        empSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Event triggered when the pad is signed
                SignpadDialog signpadDialog = new SignpadDialog();
                Bundle args = new Bundle();
                args.putInt("title", 1);
                signpadDialog.setArguments(args);
                signpadDialog.show(getActivity().getSupportFragmentManager(), "empSign");
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error_message = "";
                error_message = checkInputFormat();
                if (error_message == "") {
                    JSONArray invoice = new JSONArray();

                    //remark data
                    try {
                        JSONObject remarkJson = new JSONObject();
                        if (remark.getText().toString().isEmpty()) {
                            remarkJson.put("remark", JSONObject.NULL);
                        } else {
                            remarkJson.put("remark", remark.getText().toString());
                        }
                        invoice.put(remarkJson);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get remark data json exception");
                        Log.d("detail", e.toString());
                    }
                    //amount data
                    try {
                        JSONObject amountJson = new JSONObject();
                        amountJson.put("amount", amount.getText().toString().replace(" ", ""));
                        invoice.put(amountJson);
                    } catch (JSONException e) {
                        Log.d("save onclick error", "get amount data json exception");
                        Log.d("detail", e.toString());
                    }
                    //custsign photo
                    try {
                        FileWrapper fw = new FileWrapper(getContext(), FileWrapper.Storage.INTERNAL, "sign");
                        fw.copyForm(((BitmapDrawable) custSign.getDrawable()).getBitmap(), Bitmap.CompressFormat.JPEG, 100, FileWrapper.Behavior.CREATE_ALWAYS);
                        JSONObject custSignJson = new JSONObject();
                        custSignJson.put("custsign", fw.getBase64String());
                        invoice.put(custSignJson);
                    } catch (ClassCastException e){
                        e.printStackTrace();
                        error_message = "Customer signature cannot be empty!";
                    }  catch (JSONException e) {
                        Log.d("save onclick error", "get custsign photo json exception");
                        Log.d("detail", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //custsign photo
                    try {
                        FileWrapper fw = new FileWrapper(getContext(), FileWrapper.Storage.INTERNAL, "sign");
                        fw.copyForm(((BitmapDrawable) empSign.getDrawable()).getBitmap(), Bitmap.CompressFormat.JPEG, 100, FileWrapper.Behavior.CREATE_ALWAYS);
                        JSONObject empSignJson = new JSONObject();
                        empSignJson.put("empsign", fw.getBase64String());
                        invoice.put(empSignJson);
                    } catch (ClassCastException e){
                        e.printStackTrace();
                        if(error_message == "")
                            error_message = "Employee signature cannot be empty!";
                    }  catch (JSONException e) {
                        Log.d("save onclick error", "get custsign photo json exception");
                        Log.d("detail", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("order form json", invoice.toString());

                    if(error_message == "") {
                        mCallback.sendInvoicMessage(invoice);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(fm.findFragmentByTag("quotationInvoice")).commit();
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
        if(empSign.getDrawable() == null) {
            error_message = "Employee signature cannot be empty!";
        }
        if(custSign.getDrawable() == null) {
            error_message = "Customer signature cannot be empty!";
        }
        if(!isNumeric(amount.getText().toString().replace(" ", ""))) {
            error_message = "Amount value only can be type in numeric with no more 2 dec. places!";
        }
        if((amount.getText().toString()).replace(" ", "").isEmpty()) {
            error_message = "Amount value cannot be empty";
        }
        return error_message;
    }

    public static boolean isNumeric(String str)
    {
        return str.replace(" ", "").matches("^\\d+(\\.\\d\\d)?$") || str.replace(" ", "").matches("^\\d+(\\.\\d)?$");  //match a number with no more then 2 decimal place
    }
}
