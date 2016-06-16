package com.geeksdeck.firebasesample.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.geeksdeck.firebasesample.SampleApplication;


public class PrefsHelper {
    public static final String SHARED_PREFERENCES = "SamplePrefs";
    private static final SharedPreferences sSharedPreferences = SampleApplication
            .getInstance().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
    private static final String SHARED_NAME = SHARED_PREFERENCES + ".UserName";
    private static final String SHARED_EMAIL = SHARED_PREFERENCES + ".UserEmail";
    private static final String SHARED_ID_TOKEN = SHARED_PREFERENCES + ".UserIdToken";

    public static void clearPrefs() {
        sSharedPreferences.edit().clear().commit();
    }

    public static String getUserName() {
        return sSharedPreferences.getString(SHARED_NAME, "");
    }

    public static void setUserName(String name) {
        sSharedPreferences.edit().putString(SHARED_NAME, name).apply();
    }

    public static String getUserEmail() {
        return sSharedPreferences.getString(SHARED_EMAIL, "");
    }

    public static void setUserEmail(String email) {
        sSharedPreferences.edit().putString(SHARED_EMAIL, email).apply();
    }

    public static String getUserIdToken() {
        return sSharedPreferences.getString(SHARED_ID_TOKEN, "");
    }

    public static void setUserIdToken(String idToken) {
        sSharedPreferences.edit().putString(SHARED_ID_TOKEN, idToken).apply();
    }
}
