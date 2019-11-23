package com.example.wallpaper.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wallpaper.model.Wallpapers;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    /**
     * Wrapping the <list<Wallpapers> with LiveData
     * to avoid requiring the data every time
     **/
    private LiveData<List<Wallpapers>> wallpapers;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase dataBase = AppDatabase.getInstance(this.getApplication());
        wallpapers = dataBase.wallpaperDao().loadAllResults();
    }

    public LiveData<List<Wallpapers>> getWallpaperData() {
        return wallpapers;
    }
}