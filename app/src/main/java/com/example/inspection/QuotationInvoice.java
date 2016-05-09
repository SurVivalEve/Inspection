package com.example.inspection;


import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.gcacace.signaturepad.views.SignaturePad;

public class QuotationInvoice extends Fragment {

    private EditText remark, amount;
    private ImageView custSign, empSign;
    private Button save;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotation_invoice, container , false);
        EditText remark = (EditText) view.findViewById(R.id.remark);
        EditText amount = (EditText) view.findViewById(R.id.amount);
        custSign = (ImageView) view.findViewById(R.id.custSign);
        empSign = (ImageView) view.findViewById(R.id.empSign);
        Button save = (Button) view.findViewById(R.id.save);

        //set all button onclick listener
        setAllOnClickListener();

        return view;
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
    }
}
