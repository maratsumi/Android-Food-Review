package ateneo.food.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;

public class FoodViewPage extends AppCompatActivity {


    TextView foodTitleLabel;
    TextView foodPriceLabel;
    TextView foodStallLabel;
    TextView foodNotesLabel;
    ImageView foodImage;
    Button foodViewEditButton;
    Button foodViewDeleteButton;
    Button foodViewBackButton;
    CheckBox foodViewFavsCheckbox;
    Realm realm;
    String foodId;
    FoodReview currentFood;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_view_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    public void initViews(){
        foodTitleLabel = findViewById(R.id.foodViewTitleLabel);
        foodPriceLabel = findViewById(R.id.foodViewPriceLabel);
        foodStallLabel = findViewById(R.id.foodViewStallLabel);
        foodNotesLabel = findViewById(R.id.foodViewNotesLabel);
        foodImage = findViewById(R.id.foodViewImage);
        foodViewEditButton = findViewById(R.id.foodViewEditButton);
        foodViewDeleteButton = findViewById(R.id.foodViewDeleteButton);
        foodViewBackButton = findViewById(R.id.foodViewBackButton);
        foodViewFavsCheckbox = findViewById(R.id.foodViewFavsCheckbox);

        foodViewEditButton.setOnClickListener(v -> onEdit());
        foodViewDeleteButton.setOnClickListener(v -> onDelete());
        foodViewBackButton.setOnClickListener(v -> onBackPressed());
        foodViewFavsCheckbox.setOnClickListener(v -> onFavs());

        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("foodReview", MODE_PRIVATE);
        editor = prefs.edit();
        foodId = prefs.getString("id", null);

        loadFoodDetails();
    }

    private void loadFoodDetails() {
        currentFood = realm.where(FoodReview.class).equalTo("id", foodId).findFirst();

        if (currentFood != null && currentFood.isValid()) {
            foodTitleLabel.setText(currentFood.getTitle());
            foodPriceLabel.setText(String.format("₱%.2f", currentFood.getPrice()));
            foodStallLabel.setText(currentFood.getStall());
            foodNotesLabel.setText(currentFood.getNote());
            foodViewFavsCheckbox.setChecked(currentFood.getFavorite());

            if (currentFood.getFavorite() != null) {
                foodViewFavsCheckbox.setChecked(currentFood.getFavorite());
            } else {
                foodViewFavsCheckbox.setChecked(false);
            }

            if (currentFood.getFoodImagePath() != null) {
                File imageFile = new File(getExternalCacheDir(), currentFood.getFoodImagePath());
                if (imageFile.exists()) {
                    Picasso.get()
                            .load(imageFile)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .into(foodImage);
                } else {
                    foodImage.setImageDrawable(null);
                    Toast.makeText(this, "No image file", Toast.LENGTH_SHORT).show();
                }
            } else {
                foodImage.setImageDrawable(null);
            }
        } else {
            Toast.makeText(this, "Returning to gallery.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onEdit() {
        if (currentFood != null && currentFood.isValid()) {
            Intent intent = new Intent(this, FoodEditPage.class);
            startActivity(intent);
        }
    }

    private void onDelete() {
        if (currentFood != null && currentFood.isValid()) {
            String foodTitle = currentFood.getTitle();

            if (currentFood.getFoodImagePath() != null) {
                File imageFile = new File(getExternalCacheDir(), currentFood.getFoodImagePath());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            realm.beginTransaction();
            currentFood.deleteFromRealm();
            realm.commitTransaction();

            Toast.makeText(this, foodTitle + " deleted.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onFavs() {
        realm.beginTransaction();
        currentFood.setFavorite(foodViewFavsCheckbox.isChecked());
        realm.commitTransaction();
        Toast.makeText(this, currentFood.getTitle() + (currentFood.getFavorite() ? " added to" : " removed from") + " favorites.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (foodId != null) {
            loadFoodDetails();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
    }
}