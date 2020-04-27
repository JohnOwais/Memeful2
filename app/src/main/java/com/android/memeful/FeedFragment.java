package com.android.memeful;
// https://api.imgur.com/3/account/johnowais/gallery_favorites/0.json -> My Account Link (Owais Yosuf)

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

public class FeedFragment extends Fragment {

    private OkHttpClient httpClient = new OkHttpClient.Builder().build();
    View view;
    DatabaseHelper databaseHelper;
    int i = 0, pageNo = 0, prevIndex = 0;
    Handler handler = new Handler();
    RecyclerView recyclerView;
    Boolean load = true;
    Button loadButton;

    static class PhotoVH extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView title;
        TextView points;

        public PhotoVH(View itemView) {
            super(itemView);
        }
    }

    class Photo {
        String id;
        String title;
        String points;
    }

    final List<Photo> photos = new ArrayList<>();

    final RecyclerView.Adapter<PhotoVH> adapter = new RecyclerView.Adapter<PhotoVH>() {
        @Override
        public PhotoVH onCreateViewHolder(ViewGroup parent, int viewType) {
            PhotoVH vh = new PhotoVH(getLayoutInflater().inflate(R.layout.item, null));
            vh.photo = vh.itemView.findViewById(R.id.photo);
            vh.title = vh.itemView.findViewById(R.id.title);
            vh.points = vh.itemView.findViewById(R.id.points);
            return vh;
        }

        @Override
        public void onBindViewHolder(PhotoVH holder, final int position) {
            Picasso.with(getContext()).load("https://i.imgur.com/" +
                    photos.get(position).id + ".jpg").into(holder.photo);
            holder.title.setText(photos.get(position).title);
            holder.points.setText(photos.get(position).points);
            final Intent thread = new Intent(getContext(), ImageActivity.class);
            thread.putExtra("imageKey", position);
            thread.putExtra("size", prevIndex);
            thread.putExtra("tableNo", 2);
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(thread);
                }
            });
            holder.photo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Intent zoom = new Intent(getContext(), ZoomImage.class);
                    zoom.putExtra("image", "https://i.imgur.com/" + photos.get(position).id + ".jpg");
                    startActivity(zoom);
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return photos.size();
        }
    };

    private void render() {
        RecyclerView rv = view.findViewById(R.id.rv_of_photos_feed);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, 1);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = 10;
                outRect.left = 10;
                outRect.right = 10;
                outRect.bottom = 10;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, null);
        recyclerView = view.findViewById(R.id.rv_of_photos_feed);
        loadButton = view.findViewById(R.id.load_images);
        Toast welcome = Toast.makeText(getContext(), "Loading images...", Toast.LENGTH_LONG);
        welcome.setGravity(Gravity.CENTER, 0, 0);
        welcome.show();
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadButton.setVisibility(View.GONE);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        render();
                    }
                });
            }
        });
        databaseHelper = new DatabaseHelper(getContext());
        databaseHelper.initializeDB(2);
        getData(pageNo);
        final Toast message = Toast.makeText(getContext(), "Loading, please wait...", Toast.LENGTH_LONG);
        message.setGravity(Gravity.CENTER, 0, 0);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE && load) {
                    load = false;
                    message.show();
                    getData(++pageNo);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            load = true;
                        }
                    }, 15000);
                }
            }
        });
        return view;
    }

    public void reLoad() {
        recyclerView.scrollToPosition(0);
    }

    public void getData(int pageNo) {
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/gallery/user/rising/" + pageNo + ".json")
                .method("GET", null)
                .addHeader("Authorization", "Client-ID b87f33eb54662ed")
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "An error has occurred " + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    final JSONArray items = data.getJSONArray("data");
                    for (i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        Photo photo = new Photo();
                        if (item.getBoolean("is_album")) {
                            photo.id = item.getString("cover");
                        } else {
                            photo.id = item.getString("id");
                        }
                        photo.title = item.getString("title");
                        photo.points = item.getString("points") + " Points";
                        photos.add(photo); // Add photo to list
                        uploadImageData(Integer.toString(i + prevIndex), item.getString("id"), photo.id, photo.title, item.getString("account_url"), item.getString("views"), item.getString("ups"), item.getString("downs"), item.getString("favorite_count"));
                    }
                } catch (Exception e) {
                }
                prevIndex += i;
            }

            public void uploadImageData(String uID, String photoID, String id, String title, String userName, String views, String ups, String downs, String favCount) {
                databaseHelper.storeImageData(uID, photoID, id, title, userName, views, ups, downs, favCount, 2);
            }
        });
    }
}