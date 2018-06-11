package com.example.ashen.csmanager.Others;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import com.example.ashen.csmanager.Activities.Login;

import java.util.HashMap;

/**
 * Created by slash on 1/21/2018.
 */

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Fixit";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NAME = "name";
    public static final String KEY_ID = "id";
    public static final String KEY_TOKEN = "token";
    private static final String KEY_IS_LOGGED_IN = "IsUserLoggedIn";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void createLoginSession(String username, String id, String name, String token){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_TOKEN, token);

        editor.commit();
    }


    public void checkLogin(){

        if(!this.isLoggedIn()){

            Intent i = new Intent(_context, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }



    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        return user;
    }


    public void logoutUser(){

        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public void setLogin(boolean isUserLoggedin)
    {
        editor.putBoolean(KEY_IS_LOGGED_IN, isUserLoggedin);
        editor.commit();
    }



    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}

