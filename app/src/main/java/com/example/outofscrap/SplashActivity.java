package com.example.outofscrap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.outofscrap.ADMIN.AdminHome;
import com.example.outofscrap.USER.UserHome;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Toolbar toolbar = new Toolbar(this);
        toolbar.setVisibility(View.INVISIBLE);
        setContentView(R.layout.activity_splash);

        preferences = getSharedPreferences("SharedData", MODE_PRIVATE);

        RunAnimation();
        ;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String type = preferences.getString("type", "No logid");
                System.out.println("&&&&&&&&&&&&&&&" + type);
                switch (type) {
                    case "admin":
                        startActivity(new Intent(getApplicationContext(), AdminHome.class));
                        finish();
                        break;
                    case "user":
                        startActivity(new Intent(getApplicationContext(), UserHome.class));
                        finish();
                        break;
                    default:
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();
                }
            }
        }, 3000);
    }

    private void RunAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.scale);
        a.reset();
        TextView tv = findViewById(R.id.scrapText);
        tv.clearAnimation();
        tv.startAnimation(a);
    }
}