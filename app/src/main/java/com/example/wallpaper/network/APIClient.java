package com.example.wallpaper.network;

import com.example.wallpaper.adapter.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {


    private static final String URL = Constants.BASE_URL;
    private static com.example.wallpaper.network.APIClient mInstance;
    private Retrofit retrofit;

    public APIClient() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized APIClient getInstance() {
        if (mInstance == null) {
            mInstance = new APIClient();
        }
        return mInstance;
    }

    public APIInterface getApi() {
        return retrofit.create(APIInterface.class);
    }
}