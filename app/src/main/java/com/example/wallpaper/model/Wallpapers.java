package com.example.wallpaper.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Wallpapers implements Parcelable {

    // All dimensions of the image
    @SerializedName("urls")
    public Urls urls;

    // All special info about the photographer
    @SerializedName("user")
    public User user;

    protected Wallpapers(Parcel in) {
        urls = in.readParcelable(Urls.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Wallpapers> CREATOR = new Creator<Wallpapers>() {
        @Override
        public Wallpapers createFromParcel(Parcel in) {
            return new Wallpapers(in);
        }

        @Override
        public Wallpapers[] newArray(int size) {
            return new Wallpapers[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(urls, i);
        parcel.writeParcelable(user, i);
    }
}
