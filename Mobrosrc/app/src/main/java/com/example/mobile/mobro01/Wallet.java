package com.example.mobile.mobro01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Wallet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ((TextView) findViewById(R.id.wallet)).setText('\u20B9' + Logged_in_User.getUser().getUser_wallet());
    }
}
