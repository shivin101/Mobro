package com.example.mobile.mobro01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PackageThreeG extends Fragment {

    public static ArrayList<ListModel> ThreeGArray = new ArrayList<>();
    public static CustomAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.package_frag, container, false);

        adapter = new CustomAdapter(v.getContext(), R.layout.package_list, ThreeGArray);
        ListView listView = (ListView)v.findViewById(R.id.packageList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String amount = ((TextView) view.findViewById(R.id.amount)).getText().toString();
                String details = ((TextView) view.findViewById(R.id.details)).getText().toString();
                if (details.equals("No Packages available"))
                    return;
                if(Payment.PaymentList == null)
                    Payment.PaymentList = new ArrayList<>();
                Payment.PaymentList.add(new PaymentObject(PackageMain.current_number, amount, details));
                Intent intent = new Intent(view.getContext(), Payment.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public static PackageThreeG newInstance(String text) {

        PackageThreeG f = new PackageThreeG();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}