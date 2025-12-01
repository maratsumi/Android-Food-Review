package ateneo.food.review;

import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SearchFoodPage extends AppCompatActivity implements FoodReviewAdapter.OnViewClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_food_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();
        initViews();
    }

    // variables
    EditText searchFoodInput;
    Button searchFoodButton;
    Button searchFoodBackButton;
    RecyclerView searchFoodRecyclerView;
    Realm realm;
    FoodReviewAdapter foodReviewAdapter;
    SharedPreferences prefs;
    String currId;

    private void initViews(){
        searchFoodInput = findViewById(R.id.searchFoodInput);
        searchFoodButton = findViewById(R.id.searchFoodButton);
        searchFoodBackButton = findViewById(R.id.searchFoodBackButton);
        searchFoodRecyclerView = findViewById(R.id.searchFoodRecyclerView);

        prefs = getSharedPreferences("currentAccount", MODE_PRIVATE);
        currId = prefs.getString("id", null);

        // recyclerview setup
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        searchFoodRecyclerView.setLayoutManager(mLayoutManager);

        RealmResults<FoodReview> allReviews = realm.where(FoodReview.class).equalTo("userOwnerId", currId).findAll();
        foodReviewAdapter = new FoodReviewAdapter(this, allReviews, true, this);
        searchFoodRecyclerView.setAdapter(foodReviewAdapter);

        searchFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFood();
            }
        });

        searchFoodBackButton.setOnClickListener(v -> finish());
    }

    private void searchFood(){
        String queryText = searchFoodInput.getText().toString().trim();

        RealmQuery<FoodReview> query = realm.where(FoodReview.class).equalTo("userOwnerId", currId);
        if (!queryText.isEmpty()){
            query.equalTo("stall", queryText, Case.INSENSITIVE).equalTo("userOwnerId", currId);
        }

        RealmResults<FoodReview> results = query.findAll();

        foodReviewAdapter = new FoodReviewAdapter(this, results,true, this);
        searchFoodRecyclerView.setAdapter(foodReviewAdapter);
    }

    public void onView(FoodReview foodReview) {
        if (foodReview.isValid()) {
            getSharedPreferences("foodReview", MODE_PRIVATE)
                    .edit()
                    .putString("id", foodReview.getId())
                    .apply();

            startActivity(new Intent(this, FoodViewPage.class));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed()) {
            realm.close();
        }
    }

}