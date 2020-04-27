package com.android.memeful;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class ImageActivity extends AppCompatActivity {

    private OkHttpClient httpClient = new OkHttpClient.Builder().build();
    int ID, size, table;
    String id, title, userName;
    DatabaseHelper helper = new DatabaseHelper(this);
    Toolbar toolbar;
    TextView views, ups, downs, favCount;
    GridLayout gridLayout;
    Context context;
    CardView cardView;
    DrawerLayout.LayoutParams layoutParamsCard, layoutParamsContent, layoutParamsLinear, layoutParamsPhoto;
    TextView commentView, userView, commentUp, commentDown;
    LinearLayout linearLayout, linearLayout2;
    ImageView photo, goUP, goDOWN;
    float scale;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView zOOm;

    static class PhotoVH extends RecyclerView.ViewHolder {
        ImageView photo;

        public PhotoVH(View itemView) {
            super(itemView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        context = getApplicationContext();
        zOOm = findViewById(R.id.zoom);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        gridLayout = findViewById(R.id.gridLayoutComments);
        views = findViewById(R.id.views);
        ups = findViewById(R.id.ups);
        downs = findViewById(R.id.downs);
        favCount = findViewById(R.id.favCount);
        toolbar = findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ID = getIntent().getExtras().getInt("imageKey");
        size = getIntent().getExtras().getInt("size");
        table = getIntent().getExtras().getInt("tableNo");
        id = helper.getID(Integer.toString(ID), table);
        title = helper.getTitle(Integer.toString(ID), table);
        userName = helper.getUserName(Integer.toString(ID), table);
        views.setText(helper.getViews(Integer.toString(ID), table));
        ups.setText(helper.getUps(Integer.toString(ID), table));
        downs.setText(helper.getDowns(Integer.toString(ID), table));
        favCount.setText(helper.getFavCount(Integer.toString(ID), table));
        scale = this.getResources().getDisplayMetrics().density;
        layoutParamsCard = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParamsCard.setMargins((int) (5 * scale), (int) (3 * scale), (int) (5 * scale), 0);
        layoutParamsContent = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsContent.setMargins((int) (5 * scale), (int) (5 * scale), (int) (5 * scale), (int) (5 * scale));
        layoutParamsLinear = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParamsPhoto = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsPhoto.setMargins((int) (15 * scale), (int) (10 * scale), (int) (10 * scale), (int) (10 * scale));
        displayData();
        getComments(helper.getPhotoID(Integer.toString(ID), table));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayData();
            }
        });
    }

    public void displayData() {
        class Photo {
            String id;
            String title;
            String userName;
        }
        final List<Photo> photos = new ArrayList<>();
        final Photo photo = new Photo();
        photo.id = id;
        photo.title = title;
        photo.userName = userName;
        photos.add(photo); // Add photo to list
        final RecyclerView.Adapter<PhotoVH> adapter = new RecyclerView.Adapter<PhotoVH>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
                PhotoVH vh = new PhotoVH(getLayoutInflater().inflate(R.layout.image, null));
                vh.photo = vh.itemView.findViewById(R.id.photo);
                final Toast first = Toast.makeText(ImageActivity.this, "First image", Toast.LENGTH_SHORT);
                first.setGravity(Gravity.CENTER, 0, 0);
                final Toast last = Toast.makeText(ImageActivity.this, "Last image", Toast.LENGTH_SHORT);
                last.setGravity(Gravity.CENTER, 0, 0);
                vh.photo.setOnTouchListener(new OnSwipeImage(ImageActivity.this) {
                    public void onSwipeRight() {
                        if (ID > 0) {
                            gridLayout.removeAllViews();
                            --ID;
                            id = helper.getID(Integer.toString(ID), table);
                            title = helper.getTitle(Integer.toString(ID), table);
                            userName = helper.getUserName(Integer.toString(ID), table);
                            views.setText(helper.getViews(Integer.toString(ID), table));
                            ups.setText(helper.getUps(Integer.toString(ID), table));
                            downs.setText(helper.getDowns(Integer.toString(ID), table));
                            favCount.setText(helper.getFavCount(Integer.toString(ID), table));
                            getComments(helper.getPhotoID(Integer.toString(ID), table));
                            displayData();
                        } else {
                            first.show();
                        }
                    }

                    public void onSwipeLeft() {
                        if (ID < size - 1) {
                            gridLayout.removeAllViews();
                            ++ID;
                            id = helper.getID(Integer.toString(ID), table);
                            title = helper.getTitle(Integer.toString(ID), table);
                            userName = helper.getUserName(Integer.toString(ID), table);
                            views.setText(helper.getViews(Integer.toString(ID), table));
                            ups.setText(helper.getUps(Integer.toString(ID), table));
                            downs.setText(helper.getDowns(Integer.toString(ID), table));
                            favCount.setText(helper.getFavCount(Integer.toString(ID), table));
                            getComments(helper.getPhotoID(Integer.toString(ID), table));
                            displayData();
                        } else {
                            last.show();
                        }
                    }
                });
                return vh;
            }

            @Override
            public void onBindViewHolder(@NonNull PhotoVH holder, final int position) {
                Picasso.with(ImageActivity.this).load("https://i.imgur.com/" +
                        photos.get(position).id + ".jpg").into(holder.photo);
                setActionBarTitle(photos.get(position).title, photos.get(position).userName);
                zOOm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent zoom = new Intent(ImageActivity.this, ZoomImage.class);
                        zoom.putExtra("image", "https://i.imgur.com/" + photos.get(position).id + ".jpg");
                        startActivity(zoom);
                    }
                });
            }

            private void setActionBarTitle(String title, String userName) {
                toolbar.setTitle(title);
                toolbar.setSubtitle(userName);
            }

            @Override
            public int getItemCount() {
                return photos.size();
            }
        };

        ImageActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                render();
            }

            private void render() {
                RecyclerView rv = findViewById(R.id.rv_of_photo);
                rv.setLayoutManager(new LinearLayoutManager(ImageActivity.this));
                rv.setAdapter(adapter);
                rv.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    }
                });
            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }

    public void getComments(String photoID) {
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/gallery/" + photoID + "/comments")
                .method("GET", null)
                .addHeader("Authorization", "Client-ID b87f33eb54662ed")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "An error has occurred " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject data = new JSONObject(response.body().string());
                            final JSONArray items = data.getJSONArray("data");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                if (item.getString("comment").equals("[deleted]"))
                                    continue;
                                if (item.getString("comment").substring(0, 4).equals("http"))
                                    createImageCard(item.getString("author"), item.getString("comment"), item.getString("ups"), item.getString("downs"));
                                else
                                    createCommentCard(item.getString("author"), item.getString("comment"), item.getString("ups"), item.getString("downs"));
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });

    }

    private void createImageCard(String user, String photoID, String ups, String downs) {
        final String extension = photoID.substring(photoID.length() - 4);
        photoID = photoID.substring(0, photoID.length() - 4) + ".jpg";
        cardView = new CardView(context);
        cardView.setLayoutParams(layoutParamsCard);
        cardView.setBackgroundColor(Color.parseColor("#485664"));
        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParamsLinear);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout2 = new LinearLayout(context);
        linearLayout2.setLayoutParams(layoutParamsLinear);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.setPadding(0, 0, 0, (int) (10 * scale));
        userView = new TextView(context);
        userView.setLayoutParams(layoutParamsContent);
        userView.setText(user);
        userView.setPadding((int) (10 * scale), (int) (10 * scale), (int) (10 * scale), 0);
        userView.setTextSize(15);
        userView.setTextColor(Color.parseColor("#ffffff"));
        userView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        photo = new ImageView(context);
        photo.setLayoutParams(layoutParamsPhoto);
        Picasso.with(ImageActivity.this).load(photoID).into(photo);
        goUP = new ImageView(context);
        goUP.setImageResource(R.drawable.up);
        goUP.setPadding((int) (13 * scale), 0, 0, 0);
        commentUp = new TextView(context);
        commentUp.setText(ups);
        commentUp.setTextSize(10);
        commentUp.setTextColor(Color.parseColor("#ffffff"));
        goDOWN = new ImageView(context);
        goDOWN.setImageResource(R.drawable.down);
        goDOWN.setPadding((int) (20 * scale), 0, 0, 0);
        commentDown = new TextView(context);
        commentDown.setText(downs);
        commentDown.setTextSize(10);
        commentDown.setTextColor(Color.parseColor("#ffffff"));
        linearLayout2.addView(goUP);
        linearLayout2.addView(commentUp);
        linearLayout2.addView(goDOWN);
        linearLayout2.addView(commentDown);
        linearLayout.addView(userView);
        linearLayout.addView(photo);
        linearLayout.addView(linearLayout2);
        cardView.addView(linearLayout);
        gridLayout.addView(cardView);
    }

    @SuppressLint("ResourceType")
    private void createCommentCard(String user, String comment, String ups, String downs) {
        cardView = new CardView(context);
        cardView.setLayoutParams(layoutParamsCard);
        cardView.setBackgroundColor(Color.parseColor("#485664"));
        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(layoutParamsLinear);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout2 = new LinearLayout(context);
        linearLayout2.setLayoutParams(layoutParamsLinear);
        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout2.setPadding(0, 0, 0, (int) (10 * scale));
        userView = new TextView(context);
        userView.setLayoutParams(layoutParamsContent);
        userView.setText(user);
        userView.setPadding((int) (10 * scale), (int) (10 * scale), (int) (10 * scale), 0);
        userView.setTextSize(15);
        userView.setTextColor(Color.parseColor("#ffffff"));
        userView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        commentView = new TextView(context);
        commentView.setLayoutParams(layoutParamsContent);
        commentView.setText(comment);
        commentView.setPadding((int) (10 * scale), 0, (int) (10 * scale), 0);
        commentView.setTextSize(15);
        commentView.setTextColor(Color.parseColor("#c0c0c8"));
        goUP = new ImageView(context);
        goUP.setImageResource(R.drawable.up);
        goUP.setPadding((int) (13 * scale), 0, 0, 0);
        commentUp = new TextView(context);
        commentUp.setText(ups);
        commentUp.setTextSize(10);
        commentUp.setTextColor(Color.parseColor("#ffffff"));
        goDOWN = new ImageView(context);
        goDOWN.setImageResource(R.drawable.down);
        goDOWN.setPadding((int) (20 * scale), 0, 0, 0);
        commentDown = new TextView(context);
        commentDown.setText(downs);
        commentDown.setTextSize(10);
        commentDown.setTextColor(Color.parseColor("#ffffff"));
        linearLayout2.addView(goUP);
        linearLayout2.addView(commentUp);
        linearLayout2.addView(goDOWN);
        linearLayout2.addView(commentDown);
        linearLayout.addView(userView);
        linearLayout.addView(commentView);
        linearLayout.addView(linearLayout2);
        cardView.addView(linearLayout);
        gridLayout.addView(cardView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}