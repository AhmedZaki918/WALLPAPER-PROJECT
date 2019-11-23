package com.example.wallpaper.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "wallpaper")
public class Wallpapers implements Parcelable {


    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @NonNull
    // Id for each item
    private String id;

    // All dimensions of the image
    @SerializedName("urls")
    public Urls urls;

    // All special info about the photographer
    @SerializedName("user")
    public User user;

    // Constructor for our class
    public Wallpapers(Urls urls, User user) {
        this.urls = urls;
        this.user = user;
    }

    protected Wallpapers(Parcel in) {
        id = in.readString();
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
        parcel.writeString(id);
        parcel.writeParcelable(urls, i);
        parcel.writeParcelable(user, i);
    }

    // Getter
    public String getId() {
        return id;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }
}