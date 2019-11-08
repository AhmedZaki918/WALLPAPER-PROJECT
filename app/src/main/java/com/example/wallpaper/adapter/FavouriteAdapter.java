package com.example.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpaper.R;
import com.example.wallpaper.activity.DetailsActivity;
import com.example.wallpaper.model.Wallpapers;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * An {@link FavouriteAdapter} knows how to create a list item layout for each picture
 * in the data source (a list of {@link Wallpapers} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {

    private Context mContext;
    private List<Wallpapers> mWallpapers;

    // Constructor for our FavouriteAdapter
    public FavouriteAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // Getter method for List<Wallpapers>
    public List<Wallpapers> getmWallpapers() {
        return mWallpapers;
    }

    // Setter method for List<Wallpapers>
    public void setmWallpapers(List<Wallpapers> mWallpapers) {
        this.mWallpapers = mWallpapers;
        notifyDataSetChanged();
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param parent   The ViewGroup that these ViewHolders are contained within.
     * @param viewType Id for the list item layout
     * @return A new ViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_favourite, null, false);
        return new ViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the correct
     * indices in the list for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Get the position of the current list item
        final Wallpapers currentItem = mWallpapers.get(position);

        // String variable to get url of the picture
        String picUrl = currentItem.urls.getmRegular();

        // Display the image by Picasso library
        Picasso.with(mContext).load(picUrl)
                .into(holder.mWallpaper);

        // Set on click listener on the view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setup the intent to go to the DetailsActivity
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(Constants.INTENT_KEY, currentItem);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mWallpapers != null ? mWallpapers.size() : 0;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mWallpaper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mWallpaper = itemView.findViewById(R.id.iv_image);
        }
    }
}