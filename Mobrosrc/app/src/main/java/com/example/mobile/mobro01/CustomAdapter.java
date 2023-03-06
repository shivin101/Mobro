package com.example.mobile.mobro01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ListModel> {

    public CustomAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CustomAdapter(Context context, int resource, List<ListModel> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.package_list, null);
        }

        ListModel M = getItem(position);

        if(M != null) {
            TextView amount = (TextView) v.findViewById(R.id.amount);
            TextView details = (TextView) v.findViewById(R.id.details);

            if (amount != null) {
                amount.setText("Rs " + M.GetAmount().toString());
            }
            if (details != null) {
                details.setText(M.GetDetails());
            }
        }
        return v;
    }
}


class ListModel  {
    private Integer Amount;
    private String Details;

    ListModel(Integer Amount, String Details) {
        this.Amount = Amount;
        this.Details = Details;
    }

    public Integer GetAmount() {
        return this.Amount;
    }

    public String GetDetails() {
        return this.Details;
    }

    public void SetAmount(Integer Amount) {
        this.Amount = Amount;
    }

    public void SetDetails(String Details) {
        this.Details = Details;
    }
}
