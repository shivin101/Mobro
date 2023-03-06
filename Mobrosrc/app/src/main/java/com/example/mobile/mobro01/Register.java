package com.example.mobile.mobro01;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    public static ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loading  = new ProgressDialog(this);
        loading.setMessage("Registering new user");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
    }

    @SuppressWarnings("deprecation")
    public Integer send_request(View view) {
        EditText name, email, phone_no, pass;
        String Name, Email, Phone_no, Pass;

        name      =   (EditText)findViewById(R.id.name_reg);
        email      =   (EditText)findViewById(R.id.email_reg);
        phone_no      =    (EditText)findViewById(R.id.phone_no_reg);
        pass       =   (EditText)findViewById(R.id.pass_reg);

        if (phone_no.getText().toString().length() != 10) {
            Toast.makeText(getApplicationContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (email.getText().toString().equals("") || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (name.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter your name", Toast.LENGTH_SHORT).show();
            return -1;
        }
        if (pass.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
            return -1;
        }

        loading.show();

        // Create GetText Method
        // Get user defined values
        Name = name.getText().toString();
        Email = email.getText().toString();
        Phone_no = phone_no.getText().toString();
        Pass = pass.getText().toString();

        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(getString(R.string.registerlink));

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(0);
                nameValuePairs.add(new BasicNameValuePair("name", Name));
                nameValuePairs.add(new BasicNameValuePair("password", Pass));
                nameValuePairs.add(new BasicNameValuePair("email", Email));
                nameValuePairs.add(new BasicNameValuePair("phone_no", Phone_no));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String parsed_response = ParseResponse.parse_response(response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                if (response.getStatusLine().getStatusCode() != 200) {
                    Toast.makeText(getApplicationContext(), jsonObject.optString("Error, Could not connect to server. Please try later."), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    return -1;
                }
                if (jsonObject.optString("error").equals("true"))
                {
                    Toast.makeText(getApplicationContext(), jsonObject.optString("error_msg"), Toast.LENGTH_LONG).show();
                    return -1;
                }
            } catch (ClientProtocolException e) {
                Log.d("Register Cli exception", e.toString());
                // TODO Auto-generated catch block
            } catch (IOException e) {
                Log.d("Register IO exception", e.toString());
                // TODO Auto-generated catch block
            }
        }catch (Exception ex) {
            Log.d("exception",ex.toString());
        }
        loading.dismiss();
        Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();
        finish();
        return 0;
    }
}
