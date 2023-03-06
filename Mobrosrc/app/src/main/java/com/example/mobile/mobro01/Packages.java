package com.example.mobile.mobro01;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**  DEPRECATED UNUSED CLASS
 *   ONLY FOR DEBUGGING
 */

public class Packages extends AppCompatActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public static ArrayList<ListModel> PackageArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages);
        ((TextView)findViewById(R.id.operator)).setText(getIntent().getExtras().getString("operator"));
        String opcode = getIntent().getExtras().getString("opcode");
        String circode = getIntent().getExtras().getString("circode");
        PackageArray = new ArrayList<ListModel>();
        String[] packages = SendRequest(opcode, circode);
        if(packages[0] == "Connection Error") {
            Toast.makeText(Packages.this, "Error packages could not be fetched", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        // ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, packages);
        // ListView listDisplay = (ListView)findViewById(R.id.packageList);
        // listDisplay.setAdapter(listAdapter);
//
//        listDisplay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                String message = ((TextView)view).getText().toString();
//                if(message.equals("Connection Error"))
//                    return;
//                Intent intent = new Intent(Packages.this, Payment.class);
//                intent.putExtra("cost", message);
//                startActivity(intent);
//            }
//        });

        CustomAdapter adapter = new CustomAdapter(this, R.layout.package_list, PackageArray);
        ListView listView = (ListView)findViewById(R.id.packageList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String amount = ((TextView)view.findViewById(R.id.amount)).getText().toString();
                String talktime = ((TextView)view.findViewById(R.id.details)).getText().toString();
                if (amount.equals("Connection Error"))
                    return;
                Intent intent = new Intent(Packages.this, Payment.class);
                intent.putExtra("cost", amount);
                intent.putExtra("talktime", talktime);
                startActivity(intent);
            }
        });

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

        private void addDrawerItems() {
        String[] osArray = { "Invite Friend", "Transactions", "Wallet", "Settings" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String option= ((TextView)view).getText().toString();
                if(option == "Invite Friend") {
                    Intent intent = new Intent(Packages.this, Invite.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(Packages.this, option, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_packages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private String[] SendRequest(String opcode, String circode) {
        String[] packages = new String[1];
        packages[0] = "Connection Error";
        try {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/topups.php");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("opcode", opcode));
                nameValuePairs.add(new BasicNameValuePair("circode", circode));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Log.d("Response", response.toString());
                String parsed_response = parse_response(response);
                Log.d("Response Parsed: ", parsed_response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                JSONArray package_json_array = jsonObject.optJSONArray("packages");
                packages = new String[package_json_array.length()];
                ListModel temp;
                for(Integer i=0; i<package_json_array.length(); i++) {
                    JSONObject offer = package_json_array.getJSONObject(i);
                    packages[i] = (offer.optString("Detail"));
                    if(packages[i].matches(".*Main.*") || packages[i].matches(".*Maximum.*")) {
                        Log.d("REGEX", "MATCHES");
                        continue;
                    }
                    temp = new ListModel(offer.optInt("Amount"), offer.optString("Detail"));
                    PackageArray.add(temp);
                }
                Log.d("Test Response: ", jsonObject.optString("opcode")+":"+jsonObject.optString("circode"));
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

        return packages;
    }
}
