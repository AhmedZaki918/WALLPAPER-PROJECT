package com.example.wallpaper.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wallpaper.BuildConfig;
import com.example.wallpaper.database.AppDatabase;
import com.example.wallpaper.database.AppExecutors;
import com.example.wallpaper.helper.CustomBroadcastReceiver;
import com.example.wallpaper.R;
import com.example.wallpaper.helper.SampleDialog;
import com.example.wallpaper.adapter.Constants;
import com.example.wallpaper.model.Wallpapers;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailsActivity extends AppCompatActivity {

    /**
     * Initialize the variables
     */
    @BindView(R.id.tv_photographer)
    TextView photographer;
    @BindView(R.id.iv_picture)
    ImageView picture;
    @BindView(R.id.iv_download)
    ImageView downloadButton;
    @BindView(R.id.iv_share)
    ImageView shareButton;
    @BindView(R.id.iv_info)
    ImageView infoButton;
    @BindView(R.id.ch_favourite)
    CheckBox favouriteButton;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;

    // variables for SharedPreferences
    private SharedPreferences StatePreferences;
    private SharedPreferences.Editor StatePrefsEditor;
    private Boolean State;
    private String photoId;


    // Create BroadcastReceiver object
    CustomBroadcastReceiver broadcastReceiver;
    ActionBar actionBar;

    // Obj from model class
    Wallpapers wallpapers;


    // String variables to store the given value passed by the intent
    private String mRegularDimensionImage;
    private String mPhotographer;
    private String mHighDimensionImage;

    // Member variable for the Database
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        // Find a reference to the AppDatabase class
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Prepare the intent to use it
        Intent intent = getIntent();
        // Get the data from the model class
        wallpapers = intent.getParcelableExtra(Constants.INTENT_KEY);

        // Reference to BroadcastReceiver
        broadcastReceiver = new CustomBroadcastReceiver();

        // Get all fields we need to show them in that activity By Intent
        mRegularDimensionImage = wallpapers.urls.getmRegular();
        mHighDimensionImage = wallpapers.urls.getmFull();
        mPhotographer = wallpapers.user.getmName();

        // Display the poster of the selected movie By Picasso library
        Picasso.with(this).load(mRegularDimensionImage).into(picture);

        // Pass the given text by intent and display it in the TextView
        photographer.setText(mPhotographer);

        // Click listener to download the image
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage(mHighDimensionImage);
                Toast.makeText(DetailsActivity.this, R.string.downloading, Toast.LENGTH_SHORT).show();
            }
        });

        // Click listener to share the image
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage(mRegularDimensionImage);
            }
        });

        // Click listener on info button
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        // Click listener on favourite button
        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWallpaper();
            }
        });


        // To save the state of CheckBox Favourite button (Check And UnChecked)
        StatePreferences = getSharedPreferences("ChkPrefs", MODE_PRIVATE);
        StatePrefsEditor = StatePreferences.edit();
        State = StatePreferences.getBoolean("CheckState", false);
        photoId = StatePreferences.getString("photoId", "id");
        if (State && photoId == wallpapers.getId()) {
            favouriteButton.setChecked(true);
        }
    }

    // Method to open alert dialog
    private void openDialog() {
        SampleDialog exampleDialog = new SampleDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    // Method to download the photo
    private void downloadImage(String URL) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/WallpaperImages");

        if (!direct.exists()) {
            direct.mkdirs();
        }
        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Downloading")
                .setDestinationInExternalPublicDir("/WallpaperPhotos", "fileName.jpg");
        mgr.enqueue(request);
    }

    // Method to share the photo
    private void shareImage(String url) {
        Picasso.with(getApplicationContext()).load(url).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(i, "Share Image"));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    // Method to get bitmap uri
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(DetailsActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    // Save the wallpaper in the favourites
    private void saveWallpaper() {

        if (favouriteButton.isChecked()) {
            // Save wallpaper in the database
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Insert the selected move to the database
                    mDb.wallpaperDao().insertWallpaper(wallpapers);
                }
            });

            // Shared prefs
            StatePrefsEditor.putBoolean("CheckState", true);
            StatePrefsEditor.putString("photoId", wallpapers.getId());
            StatePrefsEditor.commit();

            // SnackBar
            Snackbar snackbar = Snackbar
                    .make(scrollView, (R.string.added_to_favou), Snackbar.LENGTH_SHORT);
            snackbar.show();

        } else {
            // Remove wallpaper from the database
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    // Delete the selected move by it's position
                    mDb.wallpaperDao().deleteWallpaper(wallpapers);
                }
            });

            // Shared prefs
            StatePrefsEditor.putBoolean("CheckState", false);
            StatePrefsEditor.putString("photoId", "id");
            StatePrefsEditor.commit();

            // SnackBar
            Snackbar snackbar = Snackbar
                    .make(scrollView, (R.string.removed_favou), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}