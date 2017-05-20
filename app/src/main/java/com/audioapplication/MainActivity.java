package com.audioapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.audioapplication.Fragments.HomeFragment;
import com.audioapplication.Fragments.ProductInfoFragment;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener  {
    CoordinatorLayout coordinator_main;
    DrawerLayout drawer_layout;
    public static Toolbar toolbar_main;
    NavigationView navigationView;
    String TAG = "MainActivity";
    Activity activity;
    FrameLayout frame_container;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    ImageView headerImg;
    TextView header_userEmail;
    TextView header_userName;
    View headerLayout;
    public static int opened_fragment=0;
    String removeFragment = "";
    GoogleApiClient mGoogleApiClient;
    boolean should_go_back=false;
    boolean wantToExit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_main);
        coordinator_main = (CoordinatorLayout) findViewById(R.id.coordinator_main);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar_main.setTitle("AudioBits");
        setSupportActionBar(toolbar_main);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        toolbar_main.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar_main.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(GravityCompat.START);
            }
        });
        invalidateOptionsMenu();
        frame_container = (FrameLayout) findViewById(R.id.frame_container);
        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState==null) {
            transaction = fragmentManager.beginTransaction();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        activity = this;
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        header_userEmail=(TextView) headerLayout.findViewById(R.id.header_userEmail);
        header_userName=(TextView) headerLayout.findViewById(R.id.header_userName);

        Log.d(TAG,"user_email ="+AudioApplication.data.loadData("user_email"));
        Log.d(TAG,"user_name ="+AudioApplication.data.loadData("user_name"));

        header_userEmail.setText(AudioApplication.data.loadData("user_email"));
        header_userName.setText("Welcome "+AudioApplication.data.loadData("user_name"));

        set_fragment(0);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawer_layout.closeDrawers();
        item.setCheckable(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        if (id == R.id.home) {
            opened_fragment=0;
            set_fragment(0);
        }
        if (id == R.id.logout) {
            logOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOut() {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_warning).setTitle("Logout").setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AudioApplication.data.clear();
                        AudioApplication.data.saveData("user_login","");
                        logoutFromFacebook();
                        logoutFromGoogle();
                        Intent intent = new Intent(activity, SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void logoutFromGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.d(TAG,status.toString());
                    }
        });
    }

    private void logoutFromFacebook() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (ProductInfoFragment.isFragmentVisible) {
            HomeFragment.viewpagerLender.setCurrentItem(0);
            opened_fragment = 0;
        }else {
            should_go_back=true;
            if(should_go_back) {
                if (wantToExit) {
                    try {
                        MainActivity.this.finish();
                    } catch (Exception e) {
                    }
                } else {
                    wantToExit = true;
                    Snackbar.make(findViewById(R.id.coordinator_main), "Press again to exit", Snackbar.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wantToExit = false;
                        }
                    }, 2000);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.home);
        drawer_layout.closeDrawers();
    }

    private void set_fragment(int open_fragment) {
        String tag = "";
        Fragment fragment = null;
        if (open_fragment==0) {
            tag = "Home";
            if (fragmentManager.findFragmentByTag(tag) == null) {
                transaction=fragmentManager.beginTransaction();
                fragment = new HomeFragment();
                fragmentManager.popBackStack(removeFragment,FragmentManager.POP_BACK_STACK_INCLUSIVE);
                removeFragment=tag;
                transaction.addToBackStack(tag);
                transaction.replace(R.id.frame_container, fragment, tag).commit();
            }
            else {
                fragmentManager.popBackStack(tag, 0);
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"connectionResult failed");
    }
}
