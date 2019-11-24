package com.example.wallpaper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallpaper.R;
import com.example.wallpaper.activity.DetailsActivity;
import com.example.wallpaper.model.Wallpapers;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * An {@link MainAdapter} knows how to create a list item layout for each picture
 * in the data source (a list of {@link Wallpapers} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Context mContext;
    private List<Wallpapers> mWallpapers;

    // Constructor for our MainAdapter
    public MainAdapter(Context mContext, List<Wallpapers> mWallpapers) {
        this.mContext = mContext;
        this.mWallpapers = mWallpapers;
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_main, null, false);
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

        // Validates all input from servers at first
        if (picUrl.equals("")) {
            // Create toast message
            Toast toast = Toast.makeText(mContext, R.string.error_in_server, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
            // Set alternative image
            holder.mWallpaper.setImageResource(R.drawable.error_server);
        } else {
            // Display the image by Picasso library
            Picasso.with(mContext).load(picUrl)
                    .into(holder.mWallpaper);
        }

        // Set on click listener on the view
        holder.itemView.setOnClickListener(view -> {
            // Setup the intent to go to the DetailsActivity
            Intent intent = new Intent(mContext, DetailsActivity.class);
            intent.putExtra(Constants.INTENT_KEY, currentItem);
            mContext.startActivity(intent);
        });

    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our wallpapers
     */
    @Override
    public int getItemCount() {
        return mWallpapers.size();
    }

    /**
     * Cache of the children views for a list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Initialize the views
        private ImageView mWallpaper;

        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * ImageView
         *
         * @param itemView The View that you inflated in
         *                 {@link MainAdapter#onCreateViewHolder(ViewGroup, int)}
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Find a reference to the views
            mWallpaper = itemView.findViewById(R.id.iv_picture);
        }
    }
}