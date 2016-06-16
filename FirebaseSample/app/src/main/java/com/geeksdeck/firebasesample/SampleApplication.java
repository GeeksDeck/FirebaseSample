package com.geeksdeck.firebasesample;

import android.app.Application;

import com.firebase.client.Firebase;

public class SampleApplication extends Application {

    private static SampleApplication sInstance;
    public static SampleApplication getInstance(){
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Firebase.setAndroidContext(this);
    }

}
