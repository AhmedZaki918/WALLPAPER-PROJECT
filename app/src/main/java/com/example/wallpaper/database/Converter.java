package com.example.wallpaper.database;

import androidx.room.TypeConverter;

import com.example.wallpaper.model.Urls;
import com.example.wallpaper.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Converter {

    // Two converter methods for Urls Class
    @TypeConverter
    public static Urls fromStringUrls(String value) {
        Type listType = new TypeToken<Urls>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromClassUrls(Urls list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    // Two converter methods for User Class
    @TypeConverter
    public static User fromStringUser(String value) {
        Type listType = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromClassUser(User list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
