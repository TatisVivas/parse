package com.example.parse;

import android.app.Application;

import com.parse.Parse;

public class MyApp extends Application {
    
    public static final String IP_GCP = "http://SERVER_ADDRESS:1337/parse";

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("myappid").clientKey("mymasterkey") // should correspond to Application Id env variable
                .server(IP_GCP)
                .build());
    }
}
