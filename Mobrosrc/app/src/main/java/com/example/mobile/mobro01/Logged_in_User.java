package com.example.mobile.mobro01;

import java.util.ArrayList;

public class Logged_in_User {
    private static Logged_in_User User;

    private String user_uid;
    private String user_name;
    private String user_email;
    private String user_phone;
    private String wallet_amount;
    public ArrayList<Friend> friend_list = new ArrayList<>();

    private Logged_in_User() {}

    public void Set_Details(String[] Details) {
        user_uid = Details[0];
        user_name = Details[1];
        user_email = Details[2];
        user_phone = Details[3];
        wallet_amount = Details[4];
    }

    public String getUser_uid(){ return user_uid;}

    public String getUser_email(){ return user_email;}

    public String getUser_name(){ return user_name;}

    public String getUser_phone(){ return user_phone;}

    public String getUser_wallet(){ return wallet_amount;}

    public static synchronized Logged_in_User getUser() {
        if(User == null)
            User = new Logged_in_User();
        return User;
    }

    public static void Logout() {
        User.user_uid = null;
        User.user_phone = null;
        User.user_email = null;
        User.user_name = null;
        User.wallet_amount = null;
    }
}