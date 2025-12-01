package ateneo.food.review;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class FoodReviewAdapter extends RealmRecyclerViewAdapter<FoodReview, FoodReviewAdapter.ViewHolder> {

    // so both gallery and search can use the recyclerview
    public interface OnViewClickListener {
        void onView(FoodReview foodReview);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodTitleLabel;
        TextView foodStallLAbel;
        Button viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodTitleLabel = itemView.findViewById(R.id.foodRowTitleLabel);
            foodStallLAbel = itemView.findViewById(R.id.foodRowStallLabel);
            viewButton = itemView.findViewById(R.id.foodRowViewButton);
            foodImage = itemView.findViewById(R.id.foodImage);
        }
    }

    AppCompatActivity activity; //so that SearchFoodPage can also use this
    OnViewClickListener listener;

    public FoodReviewAdapter(AppCompatActivity activity, @Nullable OrderedRealmCollection<FoodReview> data, boolean autoUpdate, OnViewClickListener listener) {
        super(data, autoUpdate);
        this.activity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = activity.getLayoutInflater().inflate(R.layout.food_row_layout, parent, false);
        FoodReviewAdapter.ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodReview foodReview = getItem(position);

        holder.foodTitleLabel.setText(foodReview.getTitle());
        holder.foodStallLAbel.setText(foodReview.getStall());

        holder.viewButton.setOnClickListener(v -> onViewClick(foodReview));

        // Checks if the image path doesnt exist or the string it returns is empty
        if (foodReview.getFoodImagePath() != null){
            File cacheDir = activity.getExternalCacheDir();
            File photo = new File(cacheDir, foodReview.getFoodImagePath());
            if (photo.exists()){
                Picasso.get()
                        .load(photo)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.foodImage);
            }
        }
        Log.d("LOOK", "I should have loaded");
    }
    public void onViewClick(FoodReview foodReview){
        if (listener != null) {
            listener.onView(foodReview);
        }
    }

}
