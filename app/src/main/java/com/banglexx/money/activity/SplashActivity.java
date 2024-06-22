package com.banglexx.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.banglexx.money.R;
import com.banglexx.money.sharedpreferences.PreferencesManager;

public class SplashActivity extends BaseActivity {

    private PreferencesManager pref;
    private final int delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pref = new PreferencesManager(this);
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (pref.getBoolean("pref_is_login"))
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                finish();
            }
        }, delay );
    }
}