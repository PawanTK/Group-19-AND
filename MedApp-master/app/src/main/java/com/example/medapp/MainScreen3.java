package com.example.medapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

public class MainScreen3 extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        android.graphics.drawable.Drawable background = MainScreen3.this.getResources().getDrawable(R.drawable.color);
        getWindow().setBackgroundDrawable(background);
        setContentView(R.layout.activity_main_screen3);

        Button skip = findViewById(R.id.skip);
        ImageButton next = findViewById(R.id.next);
        ImageButton next1 = findViewById(R.id.imageButton3);
        ImageButton next2 = findViewById(R.id.imageButton4);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_left);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen3.this, MainActivity.class);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_left);
                startActivity(intent);
                finish();
                thread.stop();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen3.this, MainActivity.class);
                overridePendingTransition(R.anim.slide_out_right, R.anim.slide_out_left);
                startActivity(intent);
                finish();
                thread.stop();
            }
        });

        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen3.this, MainScreen2.class);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                startActivity(intent);
                finish();
                thread.stop();
            }
        });

        next2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainScreen3.this, MainScreen.class);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                startActivity(intent);
                finish();
                thread.stop();
            }
        });
    }
}