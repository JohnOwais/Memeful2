package com.android.memeful;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.squareup.picasso.Picasso;

public class ZoomImage extends AppCompatActivity {

    ImageView imageView;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        imageView = findViewById(R.id.zoomImage);
        image = getIntent().getExtras().getString("image");
        Picasso.with(ZoomImage.this).load(image).into(imageView);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(ZoomImage.this));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
