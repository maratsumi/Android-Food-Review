package ateneo.food.review;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class FavoriteFoodAdapter extends RecyclerView.Adapter<FavoriteFoodAdapter.FavoriteFoodViewHolder> {

    private List<FoodReview> favoriteFoodList;
    private Context context;

    public FavoriteFoodAdapter(Context context, List<FoodReview> favoriteFoodList) {
        this.context = context;
        this.favoriteFoodList = favoriteFoodList;
    }

    public void updateData(List<FoodReview> newList) {
        this.favoriteFoodList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_food, parent, false);
        return new FavoriteFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFoodViewHolder holder, int position) {
        FoodReview food = favoriteFoodList.get(position);
        holder.titleTextView.setText(food.getTitle());
        holder.stallTextView.setText(food.getStall());

        if (food.getFoodImagePath() != null && !food.getFoodImagePath().isEmpty()) {
            File imageFile = new File(context.getExternalCacheDir(), food.getFoodImagePath());
            if (imageFile.exists()) {
                Picasso.get().load(imageFile).into(holder.foodImageView);
            } else {
                holder.foodImageView.setImageDrawable(null);
            }
        } else {
            holder.foodImageView.setImageDrawable(null);
        }

        holder.viewButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodViewPage.class);
            intent.putExtra("foodId", food.getId()); // Pass String ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteFoodList.size();
    }

    public static class FavoriteFoodViewHolder extends RecyclerView.ViewHolder {
        ImageView foodImageView;
        TextView titleTextView;
        TextView stallTextView;
        Button viewButton;

        public FavoriteFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImageView = itemView.findViewById(R.id.favoriteItemImage);
            titleTextView = itemView.findViewById(R.id.favoriteItemTitle);
            stallTextView = itemView.findViewById(R.id.favoriteItemStall);
            viewButton = itemView.findViewById(R.id.favoriteItemViewButton);
        }
    }
}