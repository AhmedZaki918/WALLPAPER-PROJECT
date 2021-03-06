package com.example.wallpaper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.Constants;
import com.example.wallpaper.adapter.MainAdapter;
import com.example.wallpaper.model.Wallpapers;
import com.example.wallpaper.network.APIClient;
import com.example.wallpaper.widget.WallpaperWidgetProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    /**
     * Initialize the variables
     */
    private MainAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<Wallpapers> mWallpapers;

    @BindView(R.id.tv_empty_view)
    TextView mEmptyView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.btn_refresh)
    Button refreshButton;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.adView)
    AdView adView;
    @BindView(R.id.toolbar_favourite)
    Toolbar toolbar;
    @BindView(R.id.loading_indicator)
    ProgressBar progressBar;

    // For retrieve scroll position of the RecyclerView
    private static int index = -1;
    private static int top = -1;

    NetworkInfo networkInfo;
    FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Setup for action bar
        setSupportActionBar(toolbar);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Setup for Google ads
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        // Find a reference to the following
        mWallpapers = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Calling methods
        haveStoragePermission();
        getWallpapers();

        // Swipe to refresh the data coming from the server
        mSwipeRefreshLayout.setOnRefreshListener(() -> {

            // First check the current state of the network
            checkConnection();
            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnected()) {

                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                refreshButton.setVisibility(View.GONE);

                // Calling the method
                getWallpapers();
                mSwipeRefreshLayout.setRefreshing(false);

            } else {
                // If there is no network connection
                mSwipeRefreshLayout.setRefreshing(false);
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
                mEmptyView.setText(R.string.no_internet);
                refreshButton.setVisibility(View.VISIBLE);
            }
        });

        // Button to retry to fetch data from the server in case of (Connection lost)
        refreshButton.setOnClickListener(view -> {

            // First check the current state of the network
            checkConnection();
            // If there is a network connection, fetch data
            if (networkInfo != null && networkInfo.isConnected()) {

                progressBar.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                refreshButton.setVisibility(View.GONE);
                getWallpapers();

            } else {
                // If there is no network connection
                mEmptyView.setText(R.string.no_internet);
                Toast.makeText(MainActivity.this, R.string.connection_lost, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Check sdk version first before downloading the image ( Only in case sdk >= 23 )
     */
    private void haveStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
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


    /**
     * Displays wallpapers on the main activity by Retrofit library
     */
    private void getWallpapers() {
        // String variable to store Api key
        String apiKey = Constants.API_KEY;

        Call<List<Wallpapers>> call = APIClient.getInstance().getApi().get_wallpaper(apiKey, 30);
        call.enqueue(new Callback<List<Wallpapers>>() {
            @Override
            public void onResponse(@NonNull Call<List<Wallpapers>> call, @NonNull Response<List<Wallpapers>> response) {

                progressBar.setVisibility(View.GONE);
                mWallpapers = response.body();
                mAdapter = new MainAdapter(MainActivity.this, mWallpapers);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mAdapter);

                // Run the widget
                runWidget();
            }

            @Override
            public void onFailure(@NonNull Call<List<Wallpapers>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                mEmptyView.setText(R.string.no_internet);
                refreshButton.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.favourite_settings) {
            startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Check the state of internet connection
     */
    private void checkConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();
    }


    /**
     * Run widget
     */
    private void runWidget() {
        ArrayList<String> InPref = fillRow(mWallpapers);
        setPreferences(InPref, MainActivity.this);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(MainActivity.this, WallpaperWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        WallpaperWidgetProvider.updateAppWidget(MainActivity.this, appWidgetManager, appWidgetIds);
    }


    /**
     * Setup preferences for widget
     *
     * @param array    of string
     * @param mContext is context of an app
     */
    private void setPreferences(ArrayList<String> array, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("appWidget", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("photographers" + "_size", array.size());
        for (int i = 0; i < array.size(); i++)
            editor.putString("photographers" + "_" + i, array.get(i));
        editor.apply();
    }


    /**
     * Draw the layout of the row
     *
     * @param userList of Wallpapers class
     * @return an array list
     */
    private ArrayList<String> fillRow(List<Wallpapers> userList) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            String row = "Photo by " + userList.get(i).user.getmName();
            arrayList.add(row);
        }
        return arrayList;
    }
}