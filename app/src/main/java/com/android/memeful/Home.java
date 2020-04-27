package com.android.memeful;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    MostViralFragment mostViralFragment;
    FeedFragment feedFragment;
    TextView mostViral, feed;
    ViewPager viewPager;
    PageViewAdapter pageViewAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setEnabled(true);
            }
        }, 5000);
        mostViral = findViewById(R.id.mostViral);
        feed = findViewById(R.id.feed);
        viewPager = findViewById(R.id.fragment_container);
        pageViewAdapter = new PageViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pageViewAdapter);
        mostViral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostViralFragment = (MostViralFragment) viewPager.getAdapter().instantiateItem(viewPager, 0);
                mostViralFragment.reLoad();
                viewPager.setCurrentItem(0);
            }
        });
        feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedFragment = (FeedFragment) viewPager.getAdapter().instantiateItem(viewPager, 1);
                feedFragment.reLoad();
                viewPager.setCurrentItem(1);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                onChangeTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
            }
        });
    }

    @SuppressLint("NewApi")
    private void onChangeTab(int i) {
        if (i == 0) {
            mostViral.setTextSize(17);
            mostViral.setTextColor(getColor(R.color.White));
            feed.setTextSize(15);
            feed.setTextColor(getColor(R.color.Light));
        } else {
            feed.setTextSize(17);
            feed.setTextColor(getColor(R.color.White));
            mostViral.setTextSize(15);
            mostViral.setTextColor(getColor(R.color.Light));
        }
    }

    private void exit() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.exit, null);
        dialogBuilder.setView(dialogView);
        final Button exitButton = dialogView.findViewById(R.id.exit_button);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        exit();
    }
}