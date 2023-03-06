package com.example.mobile.mobro01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Forgot extends AppCompatActivity {

    public static ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

    }

    public void forgotPassword(View view) {
        Editable phone_no = ((EditText) findViewById(R.id.email)).getText();
        if (phone_no.toString() == "" || phone_no.toString().length() != 10) {
            Toast.makeText(getApplicationContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        loading  = new ProgressDialog(this);
        loading.setMessage("Sending request");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setProgress(0);
        loading.setMax(100);
        loading.show();

        try {
            new ForgotReq(Forgot.this).execute(phone_no.toString());
        }
        catch(Exception ex) {
            Log.v("EditText", "Unsupported Encoding Exception handler to be inserted.");
        }
    }

}

class ForgotReq extends AsyncTask<String, Void, String[]> {

    private Activity activity;

    public ForgotReq(Activity activity) {
        this.activity = activity;
    }

    public static String convertStreamToString(java.io.InputStream is){
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next():"";
    }

    public String parse_response(HttpResponse response) {
        String result = "";
        HttpEntity entity = response.getEntity();
        try {
            if (entity != null) {
                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                // now you have the string representation of the HTML request
                System.out.println("RESPONSE: " + result);
                instream.close();
                if (response.getStatusLine().getStatusCode() == 200) {
                    /** Insert Code to check status here. */
                }
            }
        }
        catch (Exception exec) {

        }
        // Headers
        org.apache.http.Header[] headers = response.getAllHeaders();
        return result;
    }

    private String[] SendRequest(String ph_number) {
        Boolean error =Boolean.TRUE;
        String message="Network could not be found";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/login/recover.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("phone_no", ph_number));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                Log.d("Number",ph_number);
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Response", response.toString());
                String parsed_response = parse_response(response);
                Log.d("Response Parsed: ", parsed_response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                error = jsonObject.optBoolean("error");
                message = jsonObject.optString("message");
            } catch (ClientProtocolException e) {
                Log.d("exception",e.toString());
                // TODO Auto-generated catch block
            } catch (IOException e) {
                Log.d("I/O exception",e.toString());
                // TODO Auto-generated catch block
            }
        }catch (Exception ex) {
            Log.d("exception",ex.toString());
        }

        String[] codes = new String[2];
        codes[0]=error.toString();
        codes[1]=message;
        return codes;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public String[] doInBackground(String... params) {
        String phone_no= params[0];
        return SendRequest(phone_no);
    }

    @Override
    public void onPostExecute(String[] codes) {
        Forgot.loading.dismiss();
        Toast.makeText(activity, codes[1], Toast.LENGTH_LONG).show();
    }
}