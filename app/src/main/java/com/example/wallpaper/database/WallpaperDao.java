package com.example.wallpaper.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.wallpaper.model.Wallpapers;

import java.util.List;

@Dao
public interface WallpaperDao {

    @Query("SELECT * FROM wallpaper")
    LiveData<List<Wallpapers>> loadAllResults();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWallpaper(Wallpapers wallpapers);

    @Delete
    void deleteWallpaper(Wallpapers wallpapers);
}