package com.example.mobile.mobro01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {
    public static ProgressDialog loading;

    public void Login_button(View view) {
        Editable phone_no = ((EditText) findViewById(R.id.phone_number)).getText();
        Editable password = ((EditText) findViewById(R.id.password_field)).getText();
        if (phone_no.length() != 10) {
            Toast.makeText(view.getContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.toString().equals("")) {
            Toast.makeText(view.getContext(), "Enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        loading.setMessage("Logging in");
        loading.show();

        try {
            new doLogin(Login.this).execute(phone_no.toString(), password.toString());
        }
        catch(Exception ex) {
            Log.v("EditText", "Unsupported Encoding Exception handler to be inserted.");
        }
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(Login.this, Forgot.class);
        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loading  = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
    }
}

class doLogin extends AsyncTask<String, Void, String[]> {

    private Activity activity;

    public doLogin(Activity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("deprecation")
    private String[] SendRequest(String ph_number, String password) {
        String[] user_details = new String[5];
        user_details[0] = "ERROR";
        user_details[1] = "Login could not be completed.";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/login/login.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(0);
                nameValuePairs.add(new BasicNameValuePair("phone_no", ph_number));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String parsed_response = ParseResponse.parse_response(response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                if(jsonObject.optBoolean("error") == Boolean.TRUE) {
                    user_details[1] = jsonObject.optString("error_msg");
                    return user_details;
                }
                user_details[0] = jsonObject.optString("uid");
                user_details[1] = jsonObject.optString("name");
                user_details[2] = jsonObject.optString("email");
                user_details[3] = jsonObject.optString("phone_no");
                user_details[4] = jsonObject.optString("wallet");
            } catch (ClientProtocolException e) {
                Log.d("exception", e.toString());
                // TODO Auto-generated catch block
            } catch (IOException e) {
                Log.d("I/O exception", e.toString());
                // TODO Auto-generated catch block
            }
        } catch (Exception ex) {
            Log.d("exception", ex.toString());
        }
        return user_details;
    }

    @SuppressWarnings("deprecation")
    private String getFriends() {
        String error = "Network could not be reached";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/friends/getfriends.php");
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("phone_no", Logged_in_User.getUser().getUser_phone()));
                nameValuePairs.add(new BasicNameValuePair("uid", Logged_in_User.getUser().getUser_uid()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String parsed_response = ParseResponse.parse_response(response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                if(jsonObject.optBoolean("error") == Boolean.TRUE)
                    error = jsonObject.optString("error_msg");
                else
                    error = "NOERR";

                JSONArray package_json_array = jsonObject.optJSONArray("friends");
                Logged_in_User.getUser().friend_list = new ArrayList<>(0);
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject friend = package_json_array.getJSONObject(i);
                    Logged_in_User.getUser().friend_list.add(new Friend(friend.optString("name"), friend.optString("phone")));
                    }
                } catch (ClientProtocolException e) {
                Log.d("exception", e.toString());
                // TODO Auto-generated catch block
            } catch (IOException e) {
                Log.d("I/O exception", e.toString());
                // TODO Auto-generated catch block
            }
        } catch (Exception ex) {
            Log.d("exception", ex.toString());
        }
        return error;
    }

    @Override
    public String[] doInBackground(String... params) {
        String ph_number = params[0];
        String password = params[1];
//        String[] test = new String[5];
//        test[0] = "5625e1f1042402.42205056";
//        test[1] = "Adarsh";
//        test[2] = "adarshsanjeev@gmail.com";
//        test[3] = "9704170702";
//        test[4] = "100";
        return SendRequest(ph_number, password);
    }

    @Override
    public void onPostExecute(String[] codes) {
        Login.loading.dismiss();
        Intent intent = new Intent(activity, Home.class);
        if (codes[0].equals("ERROR")) {
            Toast.makeText(activity, codes[1], Toast.LENGTH_SHORT).show();
        }
        else {
            Logged_in_User user = Logged_in_User.getUser();
            user.Set_Details(codes);
            if (!getFriends().equals("NOERR"))
                Toast.makeText(activity, codes[1], Toast.LENGTH_SHORT).show();
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
