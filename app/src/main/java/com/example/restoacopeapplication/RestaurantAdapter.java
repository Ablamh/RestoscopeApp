package com.example.restoacopeapplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.bumptech.glide.Glide;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import android.util.Log;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurants = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView restaurantImage;
        private final TextView restaurantName;
        private final TextView restaurantDescription;

        ViewHolder(View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurantImage);
            restaurantName = itemView.findViewById(R.id.restaurantName);
            restaurantDescription = itemView.findViewById(R.id.restaurantDescription);
        }

        void bind(Restaurant restaurant) {
            restaurantName.setText(restaurant.getName());
            restaurantDescription.setText(restaurant.getAdresse());

            if (restaurant.getPhotos() != null) {
                Uri photoUri = Uri.parse(restaurant.getPhotos());
                try {
                    Glide.with(itemView.getContext())
                            .load(photoUri)
                            .into(restaurantImage);

                    Log.d("ImageLoading", "Loading image: " + photoUri);
                } catch (Exception e) {
                    Log.e("ImageLoading", "Error loading image: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(restaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        notifyDataSetChanged();
    }
}