package com.example.wallpaper.network;

import com.example.wallpaper.model.Wallpapers;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {

    @GET("photos")
    Call<List<Wallpapers>> get_wallpaper(@Query("client_id") String key,
                                         @Query("per_page") int pages);
}
