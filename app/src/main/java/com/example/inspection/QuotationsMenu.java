package com.example.inspection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class QuotationsMenu extends Fragment {

    private Button invoice, orderForm;

    public static final String EMP_ID = "empid";

    public static QuotationsMenu newInstance(String id) {
        QuotationsMenu fragment = new QuotationsMenu();
        Bundle bundle = new Bundle();
        bundle.putString(EMP_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quotations_menu, container , false);

        invoice = (Button) view.findViewById(R.id.invoice);
        orderForm = (Button) view.findViewById(R.id.orderForm);
        invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuotationInvoice quotationInvoice = new QuotationInvoice();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, quotationInvoice).commit();
            }
        });
        orderForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuotationOrderForm quotationOrderForm = new QuotationOrderForm();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_fragment, quotationOrderForm).commit();
            }
        });
        return view;
    }
}
