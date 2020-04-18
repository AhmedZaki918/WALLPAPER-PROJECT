package com.example.wallpaper.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.preference.PreferenceManager;

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
import uk.co.senab.photoview.PhotoViewAttacher;


public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

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
    @BindView(R.id.toolbar_favourite)
    Toolbar toolbar;

    // variables for SharedPreferences
    private Boolean mState;
    String mPhotoId;

    // Create BroadcastReceiver object
    private CustomBroadcastReceiver broadcastReceiver;
    ActionBar actionBar;

    // Obj from model class
    private Wallpapers wallpapers;

    // String variables to store the given value passed by the intent
    private String mRegularDimensionImage;
    String mPhotographer;
    private String mHighDimensionImage;

    // Member variable for the Database
    private AppDatabase mDb;

    // Variable for zooming in photo
    PhotoViewAttacher photoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        // Setup for action bar
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Find a reference to the following
        mDb = AppDatabase.getInstance(getApplicationContext());
        broadcastReceiver = new CustomBroadcastReceiver();

        // Prepare the intent to use it
        Intent intent = getIntent();
        // Get the data from the model class
        wallpapers = intent.getParcelableExtra(Constants.INTENT_KEY);

        // Get all fields we need to show them in that activity By Intent
        assert wallpapers != null;
        mRegularDimensionImage = wallpapers.urls.getmRegular();
        mHighDimensionImage = wallpapers.urls.getmFull();
        mPhotographer = wallpapers.user.getmName();

        // Display the image By Picasso library
        Picasso.with(this)
                .load(mRegularDimensionImage)
                .into(picture);

        // Double click to zoom on the photo
        photoView = new PhotoViewAttacher(picture);
        photoView.update();

        // Pass the given text by intent and display it in the TextView
        photographer.setText(mPhotographer);

        // Click listener on views
        downloadButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        infoButton.setOnClickListener(this);
        favouriteButton.setOnClickListener(this);

        // To save the state of CheckBox Favourite button (Check And UnChecked)
        // Save the boolean state
        mState = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("checkBox1", false);
        // Save the id of photo
        mPhotoId = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("mPhotoId", "id");
        // If id equals to mPhotoId, save the state of checkbox
        if (mPhotoId.equals(wallpapers.getId())) {
            favouriteButton.setChecked(mState);
        }
    }

    /**
     * Open alert dialog to user.
     */
    private void openDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
        builder.setTitle(R.string.preview_quality)
                .setMessage(R.string.caption_quality)
                .setPositiveButton((R.string.cancel), (dialogInterface, i) -> {
                });
        builder.create().show();
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


    /**
     * Download the photo to save it in gallery.
     *
     * @param URL is url of the photo
     */
    private void downloadImage(String URL) {

        DownloadManager mgr = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(URL);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Downloading")
                .setDestinationInExternalPublicDir("/WallpaperPhotos", "fileName.jpg");
        assert mgr != null;
        mgr.enqueue(request);
    }


    /**
     * Share the photo to any app
     *
     * @param url is url of the photo
     */
    private void shareImage(String url) {

        // Get the photo by Picasso
        Picasso.with(getApplicationContext())
                .load(url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        // Setup by the intent to share the photo
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


    /**
     * Get bitmap uri
     *
     * @param bmp is the Bitmap
     * @return Bitmap uri
     */
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


    /**
     * Save the wallpaper in favourites screen
     */
    private void saveWallpaper() {
        // Store the state of checkbox in variable
        mState = favouriteButton.isChecked();

        if (mState) {
            // Save wallpaper in the database
            AppExecutors.getInstance().diskIO().execute(() -> {
                // Insert the selected move to the database
                mDb.wallpaperDao().insertWallpaper(wallpapers);
            });

            // Save the state of checkBox
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(wallpapers.getId(), mState)
                    .apply();
            PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean("checkBox1", mState)
                    .putString("mPhotoId", wallpapers.getId())
                    .apply();

            // SnackBar
            Snackbar.make(scrollView, R.string.added_to_favou, Snackbar.LENGTH_LONG)
                    .setAction(R.string.see_list, view -> {
                        startActivity(new Intent(DetailsActivity.this, FavouriteActivity.class));

                    }).setActionTextColor(Color.CYAN).show();

        } else {
            // Remove wallpaper from the database
            AppExecutors.getInstance().diskIO().execute(() -> {
                // Delete the selected move by it's position
                mDb.wallpaperDao().deleteWallpaper(wallpapers);
            });
            // Don't save the state of checkBox
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean("checkBox1", mState).putString("mPhotoId", "id").apply();
            // SnackBar
            Snackbar.make(scrollView, (R.string.removed_favou), Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        // Switch on id
        switch (v.getId()) {

            case R.id.iv_download:
                downloadImage(mHighDimensionImage);
                Toast.makeText(DetailsActivity.this, R.string.downloading, Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_share:
                shareImage(mRegularDimensionImage);
                break;
            case R.id.iv_info:
                openDialog();
                break;
            case R.id.ch_favourite:
                saveWallpaper();
                break;
        }
    }
}