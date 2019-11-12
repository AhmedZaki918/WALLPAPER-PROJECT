package com.example.wallpaper.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViewsService;

public class WallpaperWidgetService extends RemoteViewsService {

    public static Intent getIntent(Context context) {
        return new Intent(context, WallpaperWidgetService.class);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListViewsService(this.getApplicationContext()));

    }
}
