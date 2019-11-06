package com.example.wallpaper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.Constants;
import com.example.wallpaper.adapter.MainAdapter;
import com.example.wallpaper.model.Wallpapers;
import com.example.wallpaper.network.APIClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    /**
     * Initialize the variables
     */
    private MainAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<Wallpapers> mWallpapers;

//    private ProgressBar mProgressBar;
//    private TextView mEmptyView;

    // For retrieve scroll position of the RecyclerView
    private static int index = -1;
    private static int top = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mProgressBar = findViewById(R.id.loading_indicator);
//        mEmptyView = findViewById(R.id.tv_empty_view);

        // Find a reference to the following
        mWallpapers = new ArrayList<>();
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Calling methods
        haveStoragePermission();
        getWallpapers();
    }

    // Check sdk version first before downloading the image ( Only in case sdk >= 23 )
    private boolean haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //read current RecyclerView position
        index = mLayoutManager.findFirstVisibleItemPosition();
        View v = mRecyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - mRecyclerView.getPaddingTop());
    }

    @Override
    public void onResume() {
        super.onResume();
        //set RecyclerView position
        if (index != -1) {
            mLayoutManager.scrollToPositionWithOffset(index, top);
        }
    }

    // Displays wallpapers on the main activity
    private void getWallpapers() {
        // String variable to store Api key
        String apiKey = Constants.API_KEY;

        Call<List<Wallpapers>> call = APIClient.getInstance().getApi().get_wallpaper(apiKey, 30);
        call.enqueue(new Callback<List<Wallpapers>>() {
            @Override
            public void onResponse(Call<List<Wallpapers>> call, Response<List<Wallpapers>> response) {

                mWallpapers = response.body();
                mAdapter = new MainAdapter(MainActivity.this, mWallpapers);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<Wallpapers>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
