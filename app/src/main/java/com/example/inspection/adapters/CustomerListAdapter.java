package com.example.inspection.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.inspection.R;
import com.example.inspection.models.Customer;

import java.util.List;

/**
 * Created by User on 1/27/2016.
 */
public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Customer> customers;

    public CustomerListAdapter(Context context,  List<Customer> customers) {
        this.customers = customers;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_customer, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Customer c = customers.get(position);

        holder.setCustomer(c);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {
        //reduce findViewById
        public TextView txtFullname;
        public TextView txtAddr;
        public Customer customer;

        public ViewHolder(View itemView) {
            super(itemView);
            txtFullname = (TextView) itemView.findViewById(R.id.txtFullname);
            txtAddr = (TextView) itemView.findViewById(R.id.txtAddr);
            itemView.setOnClickListener(itemViewClicked);
        }

        public Customer getCustomer() {
            return customer;
        }

        public void setCustomer(Customer customer) {
            this.customer = customer;
        }

        private final View.OnClickListener itemViewClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //For fragment

//                CustomerDetailFragment customerDetailFragment = new CustomerDetailFragment();
//                FragmentTransaction ft = ((MainMenu)context).getSupportFragmentManager().beginTransaction();
//                Bundle bundle = new Bundle();
//                bundle.putString("custid", customer.getId());
//                customerDetailFragment.setArguments(bundle);
//                ft.replace(R.id.main_fragment, customerDetailFragment)
//                        .addToBackStack(null)
//                        .commit();
            }
        };
    }
}
