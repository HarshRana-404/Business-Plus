package com.harsh.businessplus;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    ImageView ivLogo;
    TextView tvTitle, tvTag;

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ivLogo = findViewById(R.id.iv_splash_logo);
        tvTitle = findViewById(R.id.tv_splash_title);
        tvTag = findViewById(R.id.tv_splash_tag_line);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.main_bg));

        Animation animLogo = new TranslateAnimation(-500, 0, 0, 0);
        animLogo.setDuration(300);
        Animation animTitle = new TranslateAnimation(500, 0, 0, 0);
        animTitle.setDuration(300);
        ivLogo.startAnimation(animLogo);
        tvTitle.startAnimation(animTitle);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tvTag.setVisibility(View.VISIBLE);
            }
        },300);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
                finish();
            }
        },1000);
    }
}