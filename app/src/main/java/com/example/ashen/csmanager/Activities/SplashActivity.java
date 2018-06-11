package com.example.ashen.csmanager.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.ashen.csmanager.Others.SessionManager;
import com.example.ashen.csmanager.R;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);
            @Override
            public void run() {

                if(sessionManager.isLoggedIn())
                {
                    Intent homeIntent = new Intent(SplashActivity.this, Home.class);
                    startActivity(homeIntent);
                    finish();
                }
                else
                {
                    Intent homeIntent = new Intent(SplashActivity.this, Login.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);

    }
}
