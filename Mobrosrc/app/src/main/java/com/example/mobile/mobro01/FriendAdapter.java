package com.example.mobile.mobro01;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class Friend {
    private String name;
    private String phone_no;
    public Boolean selected;

    Friend(String name, String phone_no) {
        this.name = name;
        this.phone_no = phone_no;
        selected = false;
    }

    public void setName(String name) { this.name = name;}

    public void setPhone_no(String phone_no) { this.phone_no = phone_no;}

    public String getName() { return this.name;}

    public String getPhone_no() { return this.phone_no;}
}

public class FriendAdapter extends ArrayAdapter<Friend> {
    public FriendAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public FriendAdapter(Context context, int resource, List<Friend> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.friend_invite_fragment, null);
        }

        Friend M = getItem(position);

        if(M != null) {
            TextView name = (TextView) v.findViewById(R.id.friend_name);
            TextView phoneno = (TextView) v.findViewById(R.id.friend_phoneno);

            if (name != null) {
                name.setText(M.getName());
            }

            if (phoneno != null) {
                phoneno.setText(M.getPhone_no());
            }
        }

        return v;
    }
}

class RechargeAdapter extends ArrayAdapter<Friend> {
    public RechargeAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public RechargeAdapter(Context context, int resource, List<Friend> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(final int position, final View view, final ViewGroup parent) {
        View v = view;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.friend_add_fragment, null);
        }

        Friend M = getItem(position);
        if(M != null) {
            TextView name = (TextView) v.findViewById(R.id.friend_name);
            TextView phoneno = (TextView) v.findViewById(R.id.friend_phoneno);

            if (name != null) {
                name.setText(M.getName());
            }

            if (phoneno != null) {
                phoneno.setText(M.getPhone_no());
            }
        }
        return v;
    }
}
