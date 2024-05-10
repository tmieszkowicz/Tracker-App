package com.estimote.blank;
import android.app.Application;
import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;

public class MyApplication extends Application {

    EstimoteCloudCredentials estimoteCloudCredentials =
            new EstimoteCloudCredentials("proximityapp-1cm", "6cb581a29682aba99c78eb1adf4b4e44");

}
