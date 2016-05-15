package com.example.inspection.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.inspection.QuotationInvoice;
import com.example.inspection.R;
import com.github.gcacace.signaturepad.views.SignaturePad;

public class SignpadDialog extends DialogFragment {

    private Bitmap result;
    private String[] titles = {"Customer Signature", "Employee Signature"};
    private int arg;
    private String title;
    private ImageView signImageView;
    private Bitmap sign;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try{
            arg = getArguments().getInt("title");
            title = titles[arg];
        } catch (Exception e) {
            Log.d("SignpadDialog error", "bundle error");
            Log.d("Detail:", e.toString());
        }
        try {
            for (int i = getActivity().getSupportFragmentManager().getFragments().size() - 1; i >= 0; i--) {
                if (getActivity().getSupportFragmentManager().getFragments().get(i) instanceof QuotationInvoice) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    switch (arg) {
                        case 0:
                            signImageView = (ImageView) fm.findFragmentByTag("quotationInvoice").getView().findViewById(R.id.custSign);
                            break;
                        case 1:
                            signImageView = (ImageView) fm.findFragmentByTag("quotationInvoice").getView().findViewById(R.id.empSign);
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e){
            Log.d("SignpadDialog error", "cannot find the image view or quotation invoice fragment");
            Log.d("Detail:", e.toString());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View signatureView = inflater.inflate(R.layout.custom_signpad_dialog, null);
        final SignaturePad signaturePad = (SignaturePad) signatureView.findViewById(R.id.sign);
        builder.setView(signatureView).setTitle(title)
                .setNeutralButton("clear", null)
                .setNegativeButton("save", null)
                .setPositiveButton("cancel", null);
        setCancelable(false);

        final AlertDialog view  = builder.create();

        view.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button clear = view.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button save = view.getButton(AlertDialog.BUTTON_NEGATIVE);
                Button cancel = view.getButton(AlertDialog.BUTTON_POSITIVE);
                clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signaturePad.clear();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            result = (Bitmap) signaturePad.getSignatureBitmap();
                        } catch (Exception e) {
                            Log.d("Signpad Dialog error", "null signautre");
                            Log.d("Detail", e.toString());
                        }
                        if(!signaturePad.isEmpty()) {
                            signImageView.setImageBitmap(result);
                        } else {
                            signImageView.setImageResource(android.R.color.transparent);
                        }
                        dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });
            }
        });
        return view;
    }
}
