package com.example.mobile.mobro01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

class PaymentObject {
    public String phone_no;
    public String amount;
    public String details;

    PaymentObject(String phone_no, String amount, String details) {
        this.phone_no = phone_no;
        this.amount = amount;
        this.details = details;
    }
}

public class Payment extends AppCompatActivity {
    public static ArrayList<PaymentObject> PaymentList;
    public static float totalAmount;

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public static ProgressDialog loading;
    public static ConfirmListAdapter confirmAdapter;

    private void calculateTotal() {
        totalAmount = 0;
        for(Integer i = 0; i<PaymentList.size(); i++) {
            totalAmount += Float.parseFloat(PaymentList.get(i).amount.substring(3));
            Log.d("AMOUNT", PaymentList.get(i).amount.substring(3));
        }
        ((TextView)findViewById(R.id.total_amount)).setText("Rs. " + totalAmount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Logged_in_User.getUser().getUser_uid() == null)
            finish();
        if(confirmAdapter != null) {
            confirmAdapter.notifyDataSetChanged();
            calculateTotal();
        }
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
                        Intent intent = new Intent(Payment.this, Invite.class);
                        startActivity(intent);
                    }
                    break;
                    case "Wallet": {
                        Intent intent = new Intent(Payment.this, Wallet.class);
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

    public void RechargeRequest(View view) {
        Logged_in_User user = Logged_in_User.getUser();
        String uid = user.getUser_uid();
        String phone_no = user.getUser_phone();
        loading.show();
        SendRequest(uid, Float.toString(totalAmount), phone_no);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        loading  = new ProgressDialog(this);
        loading.setMessage("Processing");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setProgress(0);
        loading.setMax(100);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if(PaymentList == null)
            PaymentList = new ArrayList<>();

        confirmAdapter = new ConfirmListAdapter(this, R.layout.layout_confirmation_fragment, PaymentList);
        ListView ConfirmList = (ListView)findViewById(R.id.final_list);
        ConfirmList.setAdapter(confirmAdapter);


        ConfirmList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle("Cancel this Recharge")
                        .setMessage("Are you sure you want to delete this recharge?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                PaymentList.remove(position);
                                confirmAdapter.notifyDataSetChanged();
                                calculateTotal();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        ArrayList<Friend> friend_list = Logged_in_User.getUser().friend_list;
        RechargeAdapter adapter = new RechargeAdapter(this, R.layout.friend_add_fragment, friend_list);
        ListView listView = (ListView)findViewById(R.id.friend_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String ph_number = ((TextView) view.findViewById(R.id.friend_phoneno)).getText().toString();

                try {
                    loading.show();
                    new GetFriendOperator(Payment.this).execute(ph_number);
                } catch (Exception ex) {
                    Log.v("Home Exception", "Unsupported Encoding Exception handler to be inserted.");
                }
            }
        });
        calculateTotal();
    }

    @SuppressWarnings("deprecation")
    public void SendRequest(String uid, String amount, String phone_no) {
        String parsed_response = "ERROR";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/transaction/control.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(0);
                nameValuePairs.add(new BasicNameValuePair("uid", uid));
                nameValuePairs.add(new BasicNameValuePair("amount", amount));
                nameValuePairs.add(new BasicNameValuePair("phone_no", phone_no));
                JSONArray recharges = new JSONArray();
                for(Integer i = 0; i<PaymentList.size(); i++) {
                    JSONObject temp = new JSONObject();
                    temp.put("amount", PaymentList.get(i).amount.substring(3));
                    temp.put("phone_no", PaymentList.get(i).phone_no);

                    recharges.put(temp);
                }
                Log.d("RECH",recharges.toString());
                nameValuePairs.add(new BasicNameValuePair("recharge", recharges.toString()));
                ((TextView)findViewById(R.id.total_amount)).setText("Rs. " + totalAmount);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                parsed_response = ParseResponse.parse_response(response);
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

        if (parsed_response.equals("ERROR")) {
            Payment.loading.dismiss();
            Toast.makeText(this, "Could not complete payment", Toast.LENGTH_SHORT).show();
            return;
        }
        String link = "";
        try {
            JSONObject jsonObject = new JSONObject(parsed_response);
            link = jsonObject.optString("link");
        }
        catch (Exception ex){
            Log.d("Payment exception", ex.toString());
        }
        Log.d("link", link);

        Intent intent = new Intent(this, PaymentPortal.class);
        intent.putExtra("link", link);
        startActivity(intent);
        finish();
    }
}

class ConfirmListAdapter extends ArrayAdapter<PaymentObject>
{
    public ConfirmListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ConfirmListAdapter(Context context, int resource, List<PaymentObject> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.layout_confirmation_fragment, null);
        }

        PaymentObject M = getItem(position);

        if(M != null) {
            TextView amount = (TextView) v.findViewById(R.id.amount);
            TextView details = (TextView) v.findViewById(R.id.details);
            TextView phone_no = (TextView) v.findViewById(R.id.phone_num);

            if (amount != null) {
                amount.setText(M.amount);
            }

            if (phone_no != null) {
                phone_no.setText(M.phone_no);
            }

            if (details != null) {
                details.setText(M.details);
            }
        }

        return v;
    }
}

class GetFriendOperator extends AsyncTask<String, Void, String[]> {

    private Activity activity;
    private HashMap<String, String> op_dict;

    public GetFriendOperator(Activity activity) {
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

    @SuppressWarnings("deprecation")
    private String[] SendRequest(String ph_number) {
        String operator="ERROR";
        String circode="";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/operator.php");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
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
        String operator = op_dict.get(codes[1]);
        if (operator == null) {
            Toast.makeText(activity, "Network not found.", Toast.LENGTH_SHORT).show();
            Payment.loading.dismiss();
            return;
        }
        if (operator.equals("ERROR") || operator.equals("NONE")) {
            Toast.makeText(activity, "Operator could not be detected.", Toast.LENGTH_SHORT).show();
            Payment.loading.dismiss();
            return;
        }

        try {
            new fetchFriendPackages(activity).execute(codes);
        }
        catch(Exception ex) {
            Log.v("Home Exception", "Unsupported Encoding Exception handler to be inserted.");
        }
    }
}


class fetchFriendPackages extends AsyncTask<String, Void, String> {

    private Activity activity;

    public fetchFriendPackages(Activity activity) {
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
        Payment.loading.dismiss();
        if(phone_no == null){
            Toast.makeText(activity, "Network not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, PackageMain.class);
        intent.putExtra("phone_no", phone_no);
        activity.startActivity(intent);
    }
}
