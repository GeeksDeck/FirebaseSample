package com.geeksdeck.firebasesample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geeksdeck.firebasesample.R;
import com.geeksdeck.firebasesample.util.PrefsHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final int SIGN_IN_REQUEST_CODE = 8765; // random, unique

    private SignInButton signInButton;
    private Button signOutButton;
    private TextView welcomeSign;
    private GoogleApiClient googleApiClient;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //Init Google Api objects
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // request token for further authorization
                .requestEmail() // request an user email for our credentials
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Init everything from view
     */
    private void initView() {
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signOutButton = (Button) findViewById(R.id.sign_out_button);
        welcomeSign = (TextView) findViewById(R.id.welcome_sign);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_REQUEST_CODE);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            signOutButton.setVisibility(View.GONE);
                            welcomeSign.setText("Thank You,\ncome again");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection to GoogleClientApi failed, please try again",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // We are expecting only a result from Sign in manager
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSingInResult(result);
        }
    }

    private void handleSingInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            signOutButton.setVisibility(View.VISIBLE);
            account = result.getSignInAccount();
            welcomeSign.setText("Welcome, \n" + account.getDisplayName());
            firebaseAuthWithGoogle(account);
        } else {
            signOutButton.setVisibility(View.GONE);
            // Let user know that signing process failed
            Toast.makeText(this, "Failure to sign in", Toast.LENGTH_SHORT).show();
        }
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            PrefsHelper.setUserName(acct.getDisplayName());
                            PrefsHelper.setUserEmail(acct.getEmail());
                            PrefsHelper.setUserIdToken(acct.getIdToken());
                            Intent goToChat = new Intent(MainActivity.this, ChatActivity.class);
                            startActivity(goToChat);
                            finish();
                        }
                    }
                });
    }
}
