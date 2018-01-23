package com.example.android.bakingtime.live;

import android.util.Log;

import com.example.android.bakingtime.infrastructure.BakingTimeApplication;
import com.squareup.otto.Bus;

abstract class BaseLiveService{
    private static final String LOG_TAG = BaseLiveService.class.getSimpleName();
    final BakingTimeApplication application;
    final BakingWebServices api;

//    public BaseLiveService(PopularMoviesApplication context, boolean autoInitialize, MovieWebServices api) {
//        super(context, autoInitialize);
//        this.application = context;
//        this.api = api;
//        Bus bus = application.getBus();
//        bus.register(this);
//    }

//    public BaseLiveService(PopularMoviesApplication context, boolean autoInitialize, boolean allowParallelSyncs, MovieWebServices api) {
//        super(context, autoInitialize, allowParallelSyncs);
//        this.application = context;
//        this.api = api;
//        Bus bus = application.getBus();
//        bus.register(this);
//    }

    BaseLiveService(BakingTimeApplication application, BakingWebServices api){
        this.application = application;
        this.api = api;
        Bus bus = application.getBus();
        bus.register(this);
        Log.w(LOG_TAG, "BaseLiveService bus.post" );
    }
}
