package ateneo.food.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

public class FoodEditPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_edit_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    ImageView foodImage;
    TextView foodTitleInput;
    TextView foodPriceInput;
    TextView foodStallInput;
    TextView foodNotesInput;
    Button cancelButton;
    Button saveButton;
    Realm realm;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currId;
    FoodReview foodReview;

    public void initViews(){
        foodImage = findViewById(R.id.foodEditImage);
        foodTitleInput = findViewById(R.id.foodEditTitleInput);
        foodPriceInput = findViewById(R.id.foodEditPriceInput);
        foodStallInput = findViewById(R.id.foodEditStallInput);
        foodNotesInput = findViewById(R.id.foodEditNotesInput);
        cancelButton = findViewById(R.id.foodEditCancelButton);
        saveButton = findViewById(R.id.foodEditSaveButton);

        cancelButton.setOnClickListener(v -> onCancel());
        saveButton.setOnClickListener(v -> onSave());
        foodImage.setOnClickListener(v -> onCamClick());

        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("foodReview", MODE_PRIVATE);
        editor = prefs.edit();
        currId = prefs.getString("id", null);

        // Looks for the FoodReview with the same ID as what came from the view/gallery page
        foodReview = realm.where(FoodReview.class).equalTo("id", currId).findFirst();

        foodTitleInput.setText(foodReview.getTitle());
        foodPriceInput.setText(foodReview.getPrice().toString());
        foodStallInput.setText(foodReview.getStall());
        foodNotesInput.setText(foodReview.getNote());

        // Load the image file based on foodImagePath
        if (foodReview.getFoodImagePath() != null) {
            try{
                File imageFile = new File(getExternalCacheDir(), foodReview.getFoodImagePath());
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            foodImage.setImageDrawable(null);
        }
    }

    public void onSave(){
        Toast toast;

        // Form validation
        if (foodTitleInput.getText().toString().isEmpty()){
            toast = Toast.makeText(this, "Title must not be blank", Toast.LENGTH_SHORT);
        } else if (foodPriceInput.getText().toString().isEmpty()){
            toast = Toast.makeText(this, "Price must not be blank", Toast.LENGTH_SHORT);
        } else if (foodStallInput.getText().toString().isEmpty()){
            toast = Toast.makeText(this, "Stall must not be blank", Toast.LENGTH_SHORT);
        } else {
            try {
                realm.beginTransaction();
                foodReview.setTitle(foodTitleInput.getText().toString());
                // REFERENCE: https://www.baeldung.com/java-string-to-float
                foodReview.setPrice(Float.valueOf(foodPriceInput.getText().toString()));
                foodReview.setStall(foodStallInput.getText().toString());
                // Notes are optional
                if (!foodNotesInput.getText().toString().isEmpty()){
                    foodReview.setNote(foodNotesInput.getText().toString());
                }
                realm.commitTransaction();

                toast = Toast.makeText(this, "Food review successfully edited", Toast.LENGTH_LONG);
                finish();
            } catch (Exception e) {
                toast = Toast.makeText(this, "Failed to save food", Toast.LENGTH_LONG);
            }
        }
        toast.show();
    }

    public void onCancel(){
        finish();
    }


    public void onCamClick(){
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    File savedImage = saveFile(jpeg, foodReview.getFoodImagePath());
                    foodReview.setFoodImagePath(foodReview.getFoodImagePath());

                    refreshImageView(foodImage, savedImage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private File saveFile(byte[] jpeg, String filename) throws IOException {
        File getImageDir = getExternalCacheDir();

        File savedImage = new File(getImageDir, filename);

        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }

    private void refreshImageView(ImageView imageView, File savedImage) {
        Picasso.get()
                .load(savedImage)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imageView);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(!realm.isClosed()){
            realm.close();
        }
    }
}