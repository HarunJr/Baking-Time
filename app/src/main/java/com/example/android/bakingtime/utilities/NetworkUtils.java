package com.example.android.bakingtime.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingtime.infrastructure.BakingTimeApplication;

import static com.example.android.bakingtime.BakingService.BakingServerRequest;

/**
 * Created by HARUN on 8/4/2017.
 */

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    public static void initNetworkConnection(BakingTimeApplication application){
        if (isNetworkAvailable(application)){
            application.getBus().post(new BakingServerRequest());

        }else {
            Toast.makeText(application, "No Internet Connection! ", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isNetworkAvailable(Context application) {
        ConnectivityManager cm = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        Log.w(LOG_TAG, "isNetworkAvailable" + networkInfo);
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

//    public static void loadImage(String url, ImageView imageView, Context mContext) {
//        Log.w(LOG_TAG, "loadImage " + url);
//
//        Picasso.with(mContext)
//                .load(PopularMoviesApplication.BASE_PICTURE_URL+url)
//                .networkPolicy(isNetworkAvailable(mContext)? NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
//                .error(android.R.drawable.stat_notify_error)
//                .placeholder(R.mipmap.ic_image_black_36dp)
//                .fit()
//                .into(imageView);
//    }
}
