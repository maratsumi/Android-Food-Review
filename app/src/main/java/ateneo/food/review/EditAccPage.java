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

public class EditAccPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_acc_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }
    ImageView userPhoto;
    Button backButton;
    Button submitButton;
    EditText userInput;
    EditText passInput;
    EditText passConfInput;
    Realm realm;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String currId;
    User userEdits;
    User user;

    public void initViews(){
        backButton = findViewById(R.id.userEditBackButton);
        submitButton = findViewById(R.id.userEditSubmitButton);
        userInput = findViewById(R.id.userEditNameInput);
        passInput = findViewById(R.id.userEditPassInput);
        passConfInput = findViewById(R.id.userEditConfInput);
        userPhoto = findViewById(R.id.userEditImage);

        backButton.setOnClickListener(v -> onBackClick());
        submitButton.setOnClickListener(v -> onSubmitClick());
        userPhoto.setOnClickListener(v -> onCamClick());

        realm = Realm.getDefaultInstance();

        prefs = getSharedPreferences("accountMgt", MODE_PRIVATE);
        editor = prefs.edit();
        currId = prefs.getString("id", null);

        user = realm.where(User.class).equalTo("id", currId).findFirst();

        userInput.setText(user.getUsername());
        passInput.setText(user.getPassword());
        passConfInput.setText(user.getPassword());

        userEdits = realm.where(User.class).equalTo("id", currId).findFirst();
        File cacheDir = getExternalCacheDir();
        File photo = new File(cacheDir, user.getId()+".jpeg");
        if (photo.exists()){
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(userPhoto);
        }
    }

    public void onBackClick(){
        finish();
    }

    public void onSubmitClick(){
        Toast toast;
        User userSearch = realm.where(User.class).equalTo("username", userInput.getText().toString()).findFirst();

        if (userInput.getText().toString().isEmpty()){
            toast = Toast.makeText(this, "Name must not be blank", Toast.LENGTH_LONG);
        } else if (!passConfInput.getText().toString().equals(passInput.getText().toString())){
            toast = Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_LONG);
        } else if (passConfInput.getText().toString().isEmpty() || passInput.getText().toString().isEmpty()) {
            toast = Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_LONG);
        } else if (userSearch != null){
            if (userSearch.getUsername().equals(user.getUsername())){
                try {
                    realm.beginTransaction();
                    userEdits.setUsername(userInput.getText().toString());
                    userEdits.setPassword(passInput.getText().toString());
                    realm.commitTransaction();

                    toast = Toast.makeText(this, "User successfully edited", Toast.LENGTH_LONG);

                    finish();
                }
                catch (Exception e){
                    toast = Toast.makeText(this, "Failed to save user", Toast.LENGTH_LONG);
                }
            } else {
                toast = Toast.makeText(this, "User already exists", Toast.LENGTH_LONG);
            }
        } else {
            try {
                realm.beginTransaction();
                userEdits.setUsername(userInput.getText().toString());
                userEdits.setPassword(passInput.getText().toString());
                realm.commitTransaction();

                toast = Toast.makeText(this, "User successfully edited", Toast.LENGTH_LONG);

                finish();
            }
            catch (Exception e){
                toast = Toast.makeText(this, "Failed to save user", Toast.LENGTH_LONG);
            }
        }
        toast.show();
    }
    public void onCamClick(){
        Intent i = new Intent(this, ImageActivity.class);
        startActivityForResult(i, REQUEST_CODE_IMAGE_SCREEN);
    }

    public static int REQUEST_CODE_IMAGE_SCREEN = 0;

    // SINCE WE USE startForResult(), code will trigger this once the next screen calls finish()
    public void onActivityResult(int requestCode, int responseCode, Intent data)
    {
        super.onActivityResult(requestCode, responseCode, data);

        if (requestCode==REQUEST_CODE_IMAGE_SCREEN)
        {
            if (responseCode==ImageActivity.RESULT_CODE_IMAGE_TAKEN)
            {
                // receieve the raw JPEG data from ImageActivity
                // this can be saved to a file or save elsewhere like Realm or online
                byte[] jpeg = data.getByteArrayExtra("rawJpeg");

                try {
                    // save rawImage to file
                    File savedImage = saveFile(jpeg, userEdits.getId()+".jpeg");

                    // load file to the image view via picasso
                    refreshImageView(userPhoto, savedImage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    private File saveFile(byte[] jpeg, String filename) throws IOException {
        // this is the root directory for the images
        File getImageDir = getExternalCacheDir();

        // just a sample, normally you have a diff image name each time
        File savedImage = new File(getImageDir, filename);


        FileOutputStream fos = new FileOutputStream(savedImage);
        fos.write(jpeg);
        fos.close();
        return savedImage;
    }

    private void refreshImageView(ImageView imageView, File savedImage) {


        // this will put the image saved to the file system to the imageview
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