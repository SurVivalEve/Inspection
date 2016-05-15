package com.example.inspection;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.inspection.Dialog.SignpadDialog;
import com.example.inspection.util.FileWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class QuotationInvoice extends Fragment {

    private EditText remark, amount;
    private ImageView custSign, empSign;
    private Button save;
    private Snackbar snackbar;
    private CoordinatorLayout quotationInvoiceLayout;

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
        quotationInvoiceLayout = (CoordinatorLayout) view.findViewById(R.id.quotationInvoiceLayout);
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
                    JSONArray invoiceJsonArray = new JSONArray();
                    JSONObject invoice = new JSONObject();

                    try {
                        //remark
                        if (remark.getText().toString().isEmpty()) {
                            invoice.put("remark", JSONObject.NULL);
                        } else {
                            invoice.put("remark", remark.getText().toString());
                        }

                        //amount
                        invoice.put("amount", amount.getText().toString().replace(" ", ""));

                        //custsign photo
                        FileWrapper fw = new FileWrapper(getContext(), FileWrapper.Storage.INTERNAL, "sign");
                        fw.copyForm(((BitmapDrawable) custSign.getDrawable()).getBitmap(), Bitmap.CompressFormat.JPEG, 100, FileWrapper.Behavior.CREATE_ALWAYS);
                        invoice.put("custsign", fw.getBase64String());

                        //custsign photo
                        fw.copyForm(((BitmapDrawable) empSign.getDrawable()).getBitmap(), Bitmap.CompressFormat.JPEG, 100, FileWrapper.Behavior.CREATE_ALWAYS);
                        invoice.put("empsign", fw.getBase64String());

                    } catch (ClassCastException e){
                        e.printStackTrace();
                        error_message = "All signature cannot be empty!";
                    }  catch (JSONException e) {
                        Log.d("save onclick error", "get json exception");
                        Log.d("detail", e.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("order form json", invoice.toString());

                    if(error_message == "") {
                        invoiceJsonArray.put(invoice);
                        mCallback.sendInvoicMessage(invoiceJsonArray);

                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Fragment fragment = fm.findFragmentByTag("quotationInvoice");
                        ft.hide(fragment).commit();
                    } else {
                        snackbar = Snackbar.make(quotationInvoiceLayout, error_message, Snackbar.LENGTH_LONG);

                        // Changing action button text color
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);

                        snackbar.show();
                    }
                } else {
                    snackbar = Snackbar.make(quotationInvoiceLayout, error_message, Snackbar.LENGTH_LONG);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);

                    snackbar.show();
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
