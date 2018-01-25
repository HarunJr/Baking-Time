package com.example.android.bakingtime.infrastructure;

import android.app.Application;

import com.example.android.bakingtime.live.Module;
import com.squareup.otto.Bus;

public class BakingTimeApplication extends Application {
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net";

    private final Bus bus;

    public BakingTimeApplication(){
        bus = new Bus();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Module.Register(this);
    }

    public Bus getBus() {
        return bus;
    }
}
