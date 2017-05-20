package com.audioapplication.Login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.audioapplication.AudioApplication;
import com.audioapplication.MainActivity;
import com.audioapplication.Networker.NetworkEvent;
import com.audioapplication.Networker.Networker;
import com.audioapplication.R;
import com.audioapplication.SplashActivity;
import com.audioapplication.Utils.Toaster;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.query.internal.LogicalFilter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import de.greenrobot.event.EventBus;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    String TAG = "LoginActivity";
    ProgressBar progressBar;
    private Button facebookButton;
    private SignInButton googleButton;
    private int RC_SIGN_IN=9001;
    private int FB_SIGN_IN=64206;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    CoordinatorLayout coordinate_login;
    private ProgressDialog mProgressDialog;
    Activity activity;
    CallbackManager callbackManager;
    Networker mNetworker;
    String uid = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        coordinate_login = (CoordinatorLayout) findViewById(R.id.coordinate_login);
        activity = this;
        facebookButton = (Button) findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener(this);
        googleButton =(SignInButton) findViewById(R.id.sign_in_button);
        googleButton.setOnClickListener(this);

        EventBus.getDefault().register(this);
        //register facebook loginManager callback which provide user login data
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"loginResult "+loginResult);
                if(AccessToken.getCurrentAccessToken() != null){
                    updateUI();
                }
            }
            @Override
            public void onCancel() {
                Toaster.showToast("Login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Snackbar.make(coordinate_login, "Some error occured, Please try again later, or check your internet connection.", Snackbar.LENGTH_LONG).show();
            }
        });

        //configure google signin options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .requestServerAuthCode(getString(R.string.server_client_id),false)
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.facebookButton:
                if (AudioApplication.data.isConnectedToInternet()) {
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email", "user_friends"));
                }else {
                    Snackbar.make(coordinate_login, "No internet conenction", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.sign_in_button:
                if (AudioApplication.data.isConnectedToInternet()) {
                    googleSignIn();
                }else{
                    Snackbar.make(coordinate_login, "No internet conenction", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplication());
        updateUI();
    }

    protected void onStart() {
        super.onStart();
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done" and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired, this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }

    }



    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("OnActivityResult ","requestcode "+requestCode + "resultCode "+resultCode);

        //for facebook sign-in
        if(requestCode==FB_SIGN_IN){
            Log.d(TAG,"facebook login - success");
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == RC_SIGN_IN){
            Log.d(TAG,"google_login - success");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    //handle sign in success or failure, if Signed in successfully, get user params and send it to server.
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct.getId()!=null) {
                uid = acct.getId();
            }

            new RetrieveTokenTask().execute(acct.getEmail());

            Log.d(TAG,"googleAccInfo= "+acct.toString());
            Log.d(TAG,"googleSignIn result - uid = "+uid);

        } else {
            Toaster.showToast("Error initializing google sign-in");
            updateUI(false);
        }

    }


    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), RC_SIGN_IN);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG,"token ="+s);
            Networker.getInstance().signIn("google",uid,s);
            updateUI(true);
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

     private void updateUI() {
        if(AccessToken.getCurrentAccessToken()!=null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    JSONObject json = response.getJSONObject();
                    try {
                        if (json != null) {
                            Log.d(TAG, "urlObejct" + json);
                            String uid=json.getString("id");
                            String accessToken= AccessToken.getCurrentAccessToken().getToken();
                            Networker.getInstance().signIn("facebook",uid,accessToken);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Snackbar snack = Snackbar.make(coordinate_login, "Oops..Some error occured, Please try again later.", Snackbar.LENGTH_LONG);
                        snack.show();
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,email,picture");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }

    //show progressdialog for google signIn
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    //hide progressdialog for google signIn
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }


    private void updateUI(boolean signedIn) {
        if (signedIn) {
            googleButton.setVisibility(View.GONE);
        } else {
            googleButton.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }


    public void onEvent(NetworkEvent event) {
        if (event.event.contains("signin")) {
            if (event.status) {
                Log.i(TAG, "signIn Success");
                goToDashBoard();
            } else {
                Log.i(TAG, "signIn failed");
                Toaster.toast("SignIn failed");
                updateUI(false);
            }
        }
    }

    private void goToDashBoard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
        LoginActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
