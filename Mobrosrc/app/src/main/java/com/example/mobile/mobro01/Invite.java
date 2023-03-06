package com.example.mobile.mobro01;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Invite extends AppCompatActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    public static ArrayList<Friend> friend_list;
    public static FriendAdapter adapter;
    public static ProgressDialog loading;

    @Override
    protected void onResume() {
        super.onResume();
        if(Logged_in_User.getUser().getUser_uid() == null)
            finish();
    }

    public void add_friend(View view) {

        String phone_no = ((EditText)findViewById(R.id.friend_phone)).getText().toString();
        String friend_name = ((EditText)findViewById(R.id.invite_name)).getText().toString();

        if (phone_no.equals("") || phone_no.length() < 10) {
            Toast.makeText(view.getContext(), "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (friend_name.equals("")) {
            Toast.makeText(view.getContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();
            return;
        }

        loading.show();

        try {
            new friendRequest(Invite.this).execute(friend_name, phone_no);
        }
        catch(Exception ex) {
            Log.v("EditText", "Unsupported Encoding Exception handler to be inserted.");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading  = new ProgressDialog(this);
        loading.setMessage("Adding Friend");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.setProgress(0);
        loading.setMax(100);

        setContentView(R.layout.activity_invite);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();
        friend_list = Logged_in_User.getUser().friend_list;
        adapter = new FriendAdapter(this, R.layout.friend_invite_fragment, friend_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ListView listView = (ListView)findViewById(R.id.friend_list);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                new AlertDialog.Builder(view.getContext())
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setTitle("Delete Friend")
                        .setMessage("Are you sure you want to delete this friend?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String friend_name = ((TextView) view.findViewById(R.id.friend_name)).getText().toString();
                                String phone_no = ((TextView) view.findViewById(R.id.friend_phoneno)).getText().toString();
                                friend_list.remove(position);
//                                adapter.notifyDataSetChanged();
                                Integer temp = position;
                                try {
                                    loading.setMessage("Deleting Friend");
                                    loading.show();
                                    new deleteRequest(Invite.this).execute(friend_name, phone_no, temp.toString());
                                } catch (Exception ex) {
                                    Log.v("EditText", "Unsupported Encoding Exception handler to be inserted.");
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });
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
                    case "Wallet": {
                        Intent intent = new Intent(Invite.this, Wallet.class);
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
}

@SuppressWarnings("deprecation")
class deleteRequest extends AsyncTask<String, Void, String> {

    private Activity activity;

    public deleteRequest(Activity activity) {
        this.activity = activity;
    }

    private String SendRequest(String friend_name, String friend_phone) {

        String error_message = "Network could not be reached";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/friends/deletefriend.php");
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<>(3);

                nameValuePairs.add(new BasicNameValuePair("uid", Logged_in_User.getUser().getUser_uid()));
                nameValuePairs.add(new BasicNameValuePair("name", friend_name));
                nameValuePairs.add(new BasicNameValuePair("phone_no", friend_phone));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                Log.d("Response", response.toString());
                String parsed_response = ParseResponse.parse_response(response);
                Log.d("Response Parsed: ", parsed_response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                if(jsonObject.optBoolean("error") == Boolean.TRUE) {
                    error_message = "Could not delete friend";
                }
                else {
                    error_message = "Deleted successfully";
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
        return error_message;
    }

    public String doInBackground(String... params) {
        String error = SendRequest(params[0], params[1]);
        if(error.equals("Deleted successfully")) {
            Integer position = Integer.parseInt(params[2]);
            Invite.friend_list.remove(position);
        }
        return error;
    }

    @Override
    public void onPostExecute(String message) {
        Invite.loading.dismiss();
        Invite.adapter.notifyDataSetChanged();
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }
}

class friendRequest extends AsyncTask<String, Void, String> {

    private Activity activity;

    public friendRequest(Activity activity) {
        this.activity = activity;
    }

    @SuppressWarnings("deprecation")
    private String SendRequest(String friend_name, String friend_phone) {

        String error_message = "Network could not be reached";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mobro.in/friends/addfriend.php");
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<>(3);

                nameValuePairs.add(new BasicNameValuePair("uid", Logged_in_User.getUser().getUser_uid()));
                nameValuePairs.add(new BasicNameValuePair("name", friend_name));
                nameValuePairs.add(new BasicNameValuePair("phone_no", friend_phone));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpclient.execute(httppost);
                Log.d("Response", response.toString());
                String parsed_response = ParseResponse.parse_response(response);
                Log.d("Response Parsed: ", parsed_response);
                JSONObject jsonObject = new JSONObject(parsed_response);
                if(jsonObject.optBoolean("error") == Boolean.TRUE) {
                    error_message = jsonObject.optString("error_msg");
                }
                else {
                    error_message = "Added successfully";
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
        return error_message;
    }

    @Override
    public String doInBackground(String... params) {
        String error = SendRequest(params[0], params[1]);
        if(error.equals("Added successfully")) {
            Invite.friend_list.add(new Friend(params[0], params[1]));
        }
        return error;
    }

    @Override
    public void onPostExecute(String message) {
        Invite.loading.dismiss();
        Invite.adapter.notifyDataSetChanged();

        if(message.equals("Added successfully")){
            ((EditText)activity.findViewById(R.id.friend_phone)).setText("");
            ((EditText)activity.findViewById(R.id.invite_name)).setText("");
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
