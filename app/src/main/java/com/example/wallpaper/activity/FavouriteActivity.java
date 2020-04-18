package com.example.wallpaper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.FavouriteAdapter;
import com.example.wallpaper.database.AppDatabase;
import com.example.wallpaper.database.AppExecutors;
import com.example.wallpaper.database.MainViewModel;
import com.example.wallpaper.model.Wallpapers;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteActivity extends AppCompatActivity {


    /**
     * Initialize the variables or views
     */
    private FavouriteAdapter mFavouriteAdapter;
    StaggeredGridLayoutManager mLayoutManager;
    private AppDatabase mDb;
    ActionBar actionBar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_favourite)
    Toolbar toolbar;
    @BindView(R.id.iv_info)
    ImageView iconInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        ButterKnife.bind(this);

        // Setup for action bar
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Find a reference to AppDatabase
        mDb = AppDatabase.getInstance(getApplicationContext());

        // Setup for RecyclerView and Adapter
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mFavouriteAdapter = new FavouriteAdapter(FavouriteActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Improve the performance for StaggedLayout with RecyclerView
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRecyclerView.setAdapter(mFavouriteAdapter);

        // Click listener on info icon
        iconInfo.setOnClickListener(view -> openDialog());

        // Swipe to delete selected wallpaper from the database
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    int position = viewHolder.getAdapterPosition();
                    List<Wallpapers> wallpapers = mFavouriteAdapter.getmWallpapers();
                    // Call deleteMovie in the movieDao at that position
                    mDb.wallpaperDao().deleteWallpaper(wallpapers.get(position));

                });
            }
        }).attachToRecyclerView(mRecyclerView);

        // Calling the method
        setupViewModel();
    }


    /**
     * To open alert dialog
     */
    private void openDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);
        builder.setTitle(R.string.deleteOpe)
                .setMessage(R.string.caption_delete)
                .setPositiveButton((R.string.cancel), (dialogInterface, i) -> {
                });
        builder.create().show();
    }


    /**
     * The operation of the ViewModel
     */
    private void setupViewModel() {
        MainViewModel viewModel = new ViewModelProvider(FavouriteActivity.this).get(MainViewModel.class);
        viewModel.getWallpaperData().observe(FavouriteActivity.this, movieData -> mFavouriteAdapter.setmWallpapers(movieData));
    }
}