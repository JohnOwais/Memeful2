package com.android.memeful;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent subhaanallah = new Intent(MainActivity.this, Home.class);
                startActivity(subhaanallah);
                finish();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        //
    }

}
