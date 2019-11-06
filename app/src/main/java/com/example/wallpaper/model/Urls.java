package com.example.wallpaper.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Urls implements Parcelable {

    // Url of the image with its maximum dimensions
    @SerializedName("full")
    private String mFull;

    // Url of the image with a width of 1080 pixels
    @SerializedName("regular")
    private String mRegular;

    protected Urls(Parcel in) {
        mFull = in.readString();
        mRegular = in.readString();
    }

    public static final Creator<Urls> CREATOR = new Creator<Urls>() {
        @Override
        public Urls createFromParcel(Parcel in) {
            return new Urls(in);
        }

        @Override
        public Urls[] newArray(int size) {
            return new Urls[size];
        }
    };

    // Getters methods
    public String getmFull() {
        return mFull;
    }

    public String getmRegular() {
        return mRegular;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mFull);
        parcel.writeString(mRegular);
    }
}
