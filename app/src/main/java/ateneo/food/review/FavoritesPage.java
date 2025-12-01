package ateneo.food.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;

public class FavoritesPage extends AppCompatActivity {

    private RecyclerView favoritesRecyclerView;
    private FavoriteFoodAdapter adapter;
    private Realm realm;
    private Button backButton;
    private TextView favoritesLabel;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        initViews();
    }

    private void initViews() {
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton = findViewById(R.id.favoritesBackButton);
        backButton.setOnClickListener(v -> onBackPressed());

        favoritesLabel = findViewById(R.id.favoritesLabel);

        SharedPreferences prefs = getSharedPreferences("currentAccount", MODE_PRIVATE);
        currentUserId = prefs.getString("id", null);

        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in! Cannot load favorites.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadFavoriteFoods();
    }

    private void loadFavoriteFoods() {
        RealmResults<FoodReview> favoriteFoods = realm.where(FoodReview.class)
                .equalTo("userOwnerId", currentUserId)
                .equalTo("favorite", true)
                .sort("title", Sort.ASCENDING)
                .findAll();

        adapter = new FavoriteFoodAdapter(this, realm.copyFromRealm(favoriteFoods));
        favoritesRecyclerView.setAdapter(adapter);

        favoriteFoods.addChangeListener(foodReviews -> {
            adapter.updateData(realm.copyFromRealm(foodReviews));
            if (foodReviews.isEmpty()) {
                Toast.makeText(this, "No favorite food items found.", Toast.LENGTH_SHORT).show();
            }
        });

        if (favoriteFoods.isEmpty()) {
            Toast.makeText(this, "No favorite food items found.", Toast.LENGTH_SHORT).show();
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