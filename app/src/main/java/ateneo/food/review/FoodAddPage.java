package ateneo.food.review;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class FoodAddPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_add_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    // variables
    ImageView foodImage;
    EditText foodTitleInput;
    EditText foodPriceInput;
    EditText foodStallInput;
    EditText foodNotesInput;
    Button cancelButton;
    Button saveButton;
    Realm realm;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String userOwnerId;

    String imagePath = null;

    public void initViews(){
        foodImage = findViewById(R.id.foodAddImage);
        foodTitleInput = findViewById(R.id.foodAddTitleInput);
        foodPriceInput = findViewById(R.id.foodAddPriceInput);
        foodStallInput = findViewById(R.id.foodAddStallInput);
        foodNotesInput = findViewById(R.id.foodAddNotesInput);
        cancelButton = findViewById(R.id.foodAddCancelButton);
        saveButton = findViewById(R.id.foodAddSaveButton);

        foodImage.setOnClickListener(v -> onCamClick());
        cancelButton.setOnClickListener(v -> onCancel());
        saveButton.setOnClickListener(v -> onSave());

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("currentAccount", MODE_PRIVATE);
        userOwnerId = prefs.getString("id",null);
    }

    public void onSave(){
        String title = foodTitleInput.getText().toString();
        String price = foodPriceInput.getText().toString();
        Float priceValue;
        String stall = foodStallInput.getText().toString();
        String notes = foodNotesInput.getText().toString();

        // prevent invalid numbers
        try {
            priceValue = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            return;
        }

        final float finalPrice = priceValue; //finalizes the price after checking

        if (title.isEmpty()){
            Toast.makeText(this, "Title must not be blank", Toast.LENGTH_SHORT).show();
            return;
        } else if (price.isEmpty()){
            Toast.makeText(this, "Price must not be blank", Toast.LENGTH_SHORT).show();
            return;
        } else if (stall.isEmpty()){
            Toast.makeText(this, "Stall must not be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.beginTransaction();
        // create FoodReview Object
        FoodReview newFood = new FoodReview();
        newFood.setTitle(title);
        newFood.setPrice(finalPrice);
        newFood.setStall(stall);
        newFood.setNote(notes);
        newFood.setFavorite(false);
        newFood.setUserOwnerId(userOwnerId);

        // save the photo with the food id number
        if (rawJpegBytes != null){
            try {
                // Error was being thrown when saving using the .max("id") method because id was a string, not int/float
                String imageFileName = newFood.getId() + ".jpeg";
                File savedImage = saveFile(rawJpegBytes, imageFileName);
                newFood.setFoodImagePath(imageFileName);
            } catch (IOException e){
                e.printStackTrace();
                Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            }
        }
        realm.copyToRealmOrUpdate(newFood);
        realm.commitTransaction();

        Toast.makeText(this, "Food review successfully added", Toast.LENGTH_SHORT).show();
        finish();
    }

    public void onCancel(){
        finish();
    }

    public void onCamClick() {
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    private byte[] rawJpegBytes = null;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_SCREEN && resultCode == ImageActivity.RESULT_CODE_IMAGE_TAKEN) {
            rawJpegBytes = data.getByteArrayExtra("rawJpeg");

            // gave variable name to do checking
            if (rawJpegBytes != null) {
                try {
                    File previewFile = saveFile(rawJpegBytes, "preview.jpeg"); // temporary filename only for previewing
                    refreshImageView(foodImage, previewFile);
                } catch (IOException e) {
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