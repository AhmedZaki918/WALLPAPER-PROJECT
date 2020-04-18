package com.example.wallpaper.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.wallpaper.R;

import java.util.ArrayList;
import java.util.List;

public class ListViewsService implements RemoteViewsService.RemoteViewsFactory {


    private Context context;
    private List<String> mPhotographerInPref;

    public ListViewsService(Context context) {
        this.context = context;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        mPhotographerInPref = getPrefs(context);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mPhotographerInPref == null) {
            return 0;
        }
        return mPhotographerInPref.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.layout_widget);
        String ing = mPhotographerInPref.get(position);
        remoteView.setTextViewText(R.id.widget_item, ing);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    /**
     * Get preferences
     *
     * @param mContext is the context of the app
     * @return String array
     */
    private ArrayList<String> getPrefs(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("appWidget", 0);
        int size = prefs.getInt("photographers" + "_size", 0);
        ArrayList<String> array = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
            array.add(prefs.getString("photographers" + "_" + i, null));
        return array;
    }
}