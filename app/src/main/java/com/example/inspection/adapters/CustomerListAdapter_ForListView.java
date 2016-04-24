package com.example.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.inspection.R;
import com.example.inspection.models.Customer;

import java.util.List;

public class CustomerListAdapter_ForListView extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<Customer> customers;

    public CustomerListAdapter_ForListView(Context context, List<Customer> customers) {
        this.customers = customers;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return customers.size();
    }

    @Override
    public Object getItem(int position) {
        return customers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Customer c = customers.get(position);
        View v = inflater.inflate(R.layout.list_item_customer, parent, false);
        TextView txtFullname = (TextView) v.findViewById(R.id.txtFullname);
        TextView txtAddr = (TextView) v.findViewById(R.id.txtAddr);
        txtFullname.setText(c.getFullname());
        //txtAddr.setText(c.getAddress());
        return v;
    }
}
