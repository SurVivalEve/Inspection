package com.example.inspection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.inspection.adapters.CustomerListAdapter;
import com.example.inspection.models.Customer;
import com.example.inspection.sync.SyncManager;

import java.util.List;

public class CustomerFragment extends Fragment {

    private RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
//        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, list));
        rv = (RecyclerView) view.findViewById(R.id.rv);
        new GetCustomerTask().execute();
        //TODO: on item click listeners
        return view;
    }

    private class GetCustomerTask extends AsyncTask<Void, Void, List<Customer>> {
        @Override
        protected List<Customer> doInBackground(Void... params) {
            //network things
            SyncManager syncManager = new SyncManager("getCustomer.php?districtID=D0014");
            return syncManager.syncCustomers();
        }

        @Override
        protected void onPostExecute(List<Customer> customers) {
            super.onPostExecute(customers);
            //ui things
            rv.setAdapter(new CustomerListAdapter(getContext(), customers));
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }
}
