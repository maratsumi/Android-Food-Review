package ateneo.food.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class FoodGalleryPage extends AppCompatActivity implements FoodReviewAdapter.OnViewClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_gallery_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }
    RecyclerView foodRecyclerView;
    Realm realm;
    Button backButton;
    Button addButton;
    Button searchButton;
    TextView viewFavsLabel;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currId;

    public void initViews(){
        foodRecyclerView = findViewById(R.id.foodGalRecyclerView);
        viewFavsLabel = findViewById(R.id.foodGalFavsLabel);
        backButton = findViewById(R.id.foodGalBackButton);
        addButton = findViewById(R.id.foodGalAddButton);
        searchButton = findViewById(R.id.foodGalSearchButton);

        // currentAccount shared pref that holds the ID of the current user
        // Assigning the ID to currId for use in this page i.e. to load what food reviews they have
        prefs = getSharedPreferences("currentAccount", MODE_PRIVATE);
        currId = prefs.getString("id", null);

        // Take note of foodReview shared pref that will hold the ID of the foodReview
        // to be used in loading the appropriate food review in the view page
        prefs = getSharedPreferences("foodReview", MODE_PRIVATE);
        editor = prefs.edit();

        backButton.setOnClickListener(v -> onBack());
        addButton.setOnClickListener(v -> onAdd());
        searchButton.setOnClickListener(v -> onSearch());
        viewFavsLabel.setOnClickListener(v -> onFavs());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        foodRecyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();

        RealmResults<FoodReview> foodList = realm.where(FoodReview.class).equalTo("userOwnerId", currId).findAll();
        FoodReviewAdapter adapter = new FoodReviewAdapter(this, foodList,true, this);
        foodRecyclerView.setAdapter(adapter);

        //Log to see ALL current foodreviews
        Log.d("FOOD", realm.where(FoodReview.class).findAll().toString());
    }

    public void onView(FoodReview foodReview){
        if (foodReview.isValid()){
            // Stores the id in the foodReview shared pref to load the appropriate food page
            editor.putString("id", foodReview.getId());
            editor.commit();
            Intent intent = new Intent(this, FoodViewPage.class);
            startActivity(intent);
        }
    }
    public void onBack(){
        finish();
    }
    public void onAdd(){
        Intent intent = new Intent(this, FoodAddPage.class);
        startActivity(intent);
    }

    public void onSearch(){
        Intent intent = new Intent(this, SearchFoodPage.class);
        startActivity(intent);
    }
    public void onFavs(){
        Intent intent = new Intent(this, FavoritesPage.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (!realm.isClosed())
        {
            realm.close();
        }
    }
}