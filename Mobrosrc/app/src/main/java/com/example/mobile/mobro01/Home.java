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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;

public class Home extends AppCompatActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public static ProgressDialog loading;

    public void linkPackages(View view) {
        findViewById(R.id.main_home_layout).requestFocus();
        Editable edit_phone = ((EditText) findViewById(R.id.phone_number)).getText();
        if (edit_phone.length() != 10) {
            Toast.makeText(view.getContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        loading.setMessage("Getting operator");
        loading.show();

        String ph_number = ((EditText)findViewById(R.id.phone_number)).getText().toString();
        try {
            new GetOperator(Home.this).execute(ph_number);
        }
        catch(Exception ex) {
            Log.v("Home Exception", "Unsupported Encoding Exception handler to be inserted.");
        }
    }

    public void myRecharge(View view) {
        loading.setMessage("Getting operator");
        loading.show();

        String ph_number = Logged_in_User.getUser().getUser_phone();

        try {
            new GetOperator(Home.this).execute(ph_number);
        }
        catch(Exception ex) {
            Log.v("Home Exception", "Unsupported Encoding Exception handler to be inserted.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        loading  = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Logged_in_User.getUser().getUser_uid() == null)
            finish();
    }

    private void addDrawerItems() {

        ArrayAdapter<String> mAdapter;
        Logged_in_User user = Logged_in_User.getUser();
        ((TextView) findViewById(R.id.userName)).setText(user.getUser_name());
        ((TextView) findViewById(R.id.userEmail)).setText(user.getUser_email());
        String[] osArray = { "Add Friend", "Wallet", "Logout" };
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String option= ((TextView)view).getText().toString();
                switch (option) {
                    case "Add Friend": {
                        Intent intent = new Intent(Home.this, Invite.class);
                        startActivity(intent);
                    }
                    break;
                    case "Wallet": {
                        Intent intent = new Intent(Home.this, Wallet.class);
                        startActivity(intent);
                    }
                    break;
                    case "Logout": {
                        Logged_in_User.Logout();
                        finish();
                    }
                    break;
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
        // Activate the navigation drawer toggle
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}

@SuppressWarnings("deprecation")
class GetOperator extends AsyncTask<String, Void, String[]> {

    private Activity activity;
    private HashMap<String, String> op_dict;

    public GetOperator(Activity activity) {
        this.activity = activity;
        op_dict = new HashMap<>();
        this.op_dict.put("28", "Airtel");
        this.op_dict.put("1", "Aircel");
        this.op_dict.put("3", "BSNL");
        this.op_dict.put("22", "Vodafone");
        this.op_dict.put("17", "Tata DOCOMO GSM");
        this.op_dict.put("18", "Tata DOCOMO CDMA");
        this.op_dict.put("13", "Reliance GSM");
        this.op_dict.put("12", "Reliance CDMA");
        this.op_dict.put("10", "MTS");
        this.op_dict.put("19", "Uninor");
        this.op_dict.put("9", "Loop");
        this.op_dict.put("5", "Videocon");
        this.op_dict.put("6", "MTNL Mumbai");
        this.op_dict.put("20", "MTNL Delhi");
        this.op_dict.put("0", "NONE");
        this.op_dict.put("8", "IDEA");
        this.op_dict.put("17", "Virgin GSM");
        this.op_dict.put("18", "Virgin CDMA");
    }

    private String[] SendRequest(String ph_number) {
        String operator="ERROR";
        String circode="";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/operator.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(0);
                nameValuePairs.add(new BasicNameValuePair("phone_no", ph_number));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                String parsed_response = ParseResponse.parse_response(response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                operator = jsonObject.optString("opcode");
                circode = jsonObject.optString("circode");
            } catch (ClientProtocolException e) {
                Log.d("Home exception",e.toString());
                // TODO Auto-generated catch block
            } catch (IOException e) {
                Log.d("Home IO exception",e.toString());
                // TODO Auto-generated catch block
            }
        }catch (Exception ex) {
            Log.d("Home exception",ex.toString());
        }

        String[] codes = new String[3];
        codes[0] = ph_number;
        codes[1] = operator;
        codes[2] = circode;
        return codes;
    }

    @Override
    public String[] doInBackground(String... params) {
        String ph_number = params[0];
        return SendRequest(ph_number);
    }

    @Override
    public void onPostExecute(String[] codes) {
        Home.loading.dismiss();
        String operator = op_dict.get(codes[1]);
        if (operator == null) {
            Toast.makeText(activity, "Network not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (operator.equals("ERROR") || operator.equals("NONE")) {
            Toast.makeText(activity, "Operator could not be detected.", Toast.LENGTH_SHORT).show();
            return;
        }

        Home.loading.setMessage("Fetching Packages");
        Home.loading.show();
        try {
            new fetchPackages(activity).execute(codes);
        }
        catch(Exception ex) {
            Log.v("Home Exception", "Unsupported Encoding Exception handler to be inserted.");
        }
//        intent.putExtra("phone_no", codes[0]);
//        intent.putExtra("opcode", codes[1]);
//        intent.putExtra("circode", codes[2]);
//        Home.loading.dismiss();
//        activity.startActivity(intent);
    }
}


class fetchPackages extends AsyncTask<String, Void, String> {

    private Activity activity;

    public fetchPackages(Activity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("deprecation")
    private String SendRequest(String opcode, String circode) {

        PackageFulltalk.FulltalkArray = new ArrayList<>();
        PackageTopups.TopupArray = new ArrayList<>();
        PackageOther.OtherArray = new ArrayList<>();
        PackageTwoG.TwoGArray = new ArrayList<>();
        PackageThreeG.ThreeGArray = new ArrayList<>();
        PackagePhone.PhoneArray = new ArrayList<>();

        if(PackageTopups.TopupArray.size() > 1)
            return "Already full";

        String error = "Network not found";

        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/testing.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(0);
                nameValuePairs.add(new BasicNameValuePair("opcode", opcode));
                nameValuePairs.add(new BasicNameValuePair("circode", circode));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String parsed_response = ParseResponse.parse_response(response);
                JSONObject jsonObject = new JSONObject(parsed_response);

                JSONArray package_json_array = jsonObject.optJSONArray("topups");
                if(package_json_array == null)
                    return error;
                else
                    error = "NOERR";
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
//                    String packages = (offer.optString("Detail"));
//                    if(packages.matches(".*Main.*") || packages.matches(".*Maximum.*")) {
//                        Log.d("REGEX", "MATCHES");
//                        continue;
//                    }
                    PackageTopups.TopupArray.add(new ListModel(offer.optInt("Amount"), offer.optString("Detail")));
                }

                package_json_array = jsonObject.optJSONArray("fulltalk");
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
//                    String packages = (offer.optString("Detail"));
//                    if(packages.matches(".*Main.*") || packages.matches(".*Maximum.*")) {
//                        Log.d("REGEX", "MATCHES");
//                        continue;
//                    }
                    PackageFulltalk.FulltalkArray.add(new ListModel(offer.optInt("Amount"), offer.optString("Detail")));
                }

                package_json_array = jsonObject.optJSONArray("2g");
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
//                    String packages = (offer.optString("Detail"));
//                    if(packages.matches(".*Main.*") || packages.matches(".*Maximum.*")) {
//                        Log.d("REGEX", "MATCHES");
//                        continue;
//                    }
                    PackageTwoG.TwoGArray.add(new ListModel(offer.optInt("Amount"), offer.optString("Detail")));
                }

                package_json_array = jsonObject.optJSONArray("3g");
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
//                    String packages = (offer.optString("Detail"));
//                    if(packages.matches(".*Main.*") || packages.matches(".*Maximum.*")) {
//                        Log.d("REGEX", "MATCHES");
//                        continue;
//                    }
                    PackageThreeG.ThreeGArray.add(new ListModel(offer.optInt("Amount"), offer.optString("Detail")));
                }

                package_json_array = jsonObject.optJSONArray("local\\/std\\/isd");
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
//                    String packages = (offer.optString("Detail"));
//                    if(packages.matches(".*Main.*") || packages.matches(".*Maximum.*")) {
//                        Log.d("REGEX", "MATCHES");
//                        continue;
//                    }
                    PackagePhone.PhoneArray.add(new ListModel(offer.optInt("Amount"), offer.optString("Detail")));
                }

                package_json_array = jsonObject.optJSONArray("other");
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
//                    String packages = (offer.optString("Detail"));
//                    if(packages.matches(".*Main.*") || packages.matches(".*Maximum.*")) {
//                        Log.d("REGEX", "MATCHES");
//                        continue;
//                    }
                    PackageOther.OtherArray.add(new ListModel(offer.optInt("Amount"), offer.optString("Detail")));
                }

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
        if(PackageOther.OtherArray.isEmpty())
            PackageOther.OtherArray.add(new ListModel(0, "No Packages available"));
        if(PackagePhone.PhoneArray.isEmpty())
            PackagePhone.PhoneArray.add(new ListModel(0, "No Packages available"));
        if(PackageTwoG.TwoGArray.isEmpty())
            PackageTwoG.TwoGArray.add(new ListModel(0, "No Packages available"));
        if(PackageFulltalk.FulltalkArray.isEmpty())
            PackageFulltalk.FulltalkArray.add(new ListModel(0, "No Packages available"));
        if(PackageTopups.TopupArray.isEmpty())
            PackageTopups.TopupArray.add(new ListModel(0, "No Packages available"));

        return error;
    }

    @Override
    public String doInBackground(String... params) {
        String opcode = params[1];
        String circode = params[2];
        String message = SendRequest(opcode, circode);
        if(message.equals("Already full") || message.equals("NOERR"))
            return params[0];
        else
            return null;
    }

    @Override
    public void onPostExecute(String phone_no) {
        Home.loading.dismiss();
        if(phone_no == null){
            Toast.makeText(activity, "Network not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, PackageMain.class);
        intent.putExtra("phone_no", phone_no);
        activity.startActivity(intent);
    }
}
