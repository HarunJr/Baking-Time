package com.example.android.bakingtime;

import android.util.Log;

public class BakingService {
    private static final String LOG_TAG = BakingService.class.getSimpleName();

    private BakingService(){
        Log.w(LOG_TAG, "BakingService: ");
    }

    public static class BakingServerRequest{

        public BakingServerRequest(){
            Log.w(LOG_TAG, "BakingServerRequest: ");

        }
    }
}
