package com.example.wallpaper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

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


    // Initialize variables
    private FavouriteAdapter mFavouriteAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private AppDatabase mDb;
    ActionBar actionBar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        ButterKnife.bind(this);

        // Run toolbar
        setSupportActionBar(toolbar);

        // Display up button in actionbar
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Find a reference to the following
        mDb = AppDatabase.getInstance(getApplicationContext());
        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mFavouriteAdapter = new FavouriteAdapter(FavouriteActivity.this);

        // Set layout manager and RecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Bind the Adapter to RecyclerView
        mRecyclerView.setAdapter(mFavouriteAdapter);

        // Swipe to delete selected wallpaper from the database
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Wallpapers> wallpapers = mFavouriteAdapter.getmWallpapers();
                        // Call deleteMovie in the movieDao at that position
                        mDb.wallpaperDao().deleteWallpaper(wallpapers.get(position));

                    }
                });

            }
        }).attachToRecyclerView(mRecyclerView);


        // Calling the method
        setupViewModel();
    }

    // The operation of the ViewModel
    public void setupViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(FavouriteActivity.this).get(MainViewModel.class);
        viewModel.getWallpaperData().observe(FavouriteActivity.this, new Observer<List<Wallpapers>>() {
            @Override
            public void onChanged(@Nullable List<Wallpapers> movieData) {
                mFavouriteAdapter.setmWallpapers(movieData);
            }
        });
    }
}
