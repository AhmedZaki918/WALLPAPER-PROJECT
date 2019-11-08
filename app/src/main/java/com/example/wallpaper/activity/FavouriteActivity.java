package com.example.wallpaper.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;

import com.example.wallpaper.R;
import com.example.wallpaper.adapter.FavouriteAdapter;
import com.example.wallpaper.database.AppDatabase;
import com.example.wallpaper.database.AppExecutors;
import com.example.wallpaper.database.MainViewModel;
import com.example.wallpaper.model.Wallpapers;

import java.util.List;

public class FavouriteActivity extends AppCompatActivity {


    // Initialize variables
    private RecyclerView mRecyclerView;
    private FavouriteAdapter mFavouriteAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private AppDatabase mDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Find a reference to the following
        mDb = AppDatabase.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.recycler_view);
        mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mFavouriteAdapter = new FavouriteAdapter(FavouriteActivity.this);

        // Set layout manager and RecyclerView
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        // Bind the Adapter to RecyclerView
        mRecyclerView.setAdapter(mFavouriteAdapter);


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
