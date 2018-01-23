package com.example.android.bakingtime.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.infrastructure.BakingTimeApplication;
import com.squareup.otto.Bus;

public class BaseActivity extends AppCompatActivity {
    BakingTimeApplication application;
    private Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (BakingTimeApplication) getApplication();
        bus = application.getBus();
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
