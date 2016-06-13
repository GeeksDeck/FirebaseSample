package com.geeksdeck.firebasesample;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


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
