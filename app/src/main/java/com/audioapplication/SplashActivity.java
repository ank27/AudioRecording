package com.audioapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.audioapplication.Login.LoginActivity;
import com.audioapplication.Utils.MarshMallowPermission;
import com.audioapplication.Utils.PlayGif;

public class SplashActivity extends Activity {
    Intent mainActivityIntent, loginIntent = null;
    Activity activity;
    CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        PlayGif pGif = (PlayGif) findViewById(R.id.gifView);
        pGif.setImageResource(R.drawable.ic_audio_mix);
        mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
        loginIntent = new Intent(SplashActivity.this, LoginActivity.class);

        countDownTimer = new CountDownTimer(3500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (AudioApplication.data.loadData("user_login").equals("")) {
                    SplashActivity.this.startActivity(loginIntent);
                    SplashActivity.this.finish();
                    SplashActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    SplashActivity.this.startActivity(mainActivityIntent);
                    SplashActivity.this.finish();
                    SplashActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        };
        countDownTimer.start();
    }
}
