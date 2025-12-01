package ateneo.food.review;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        checkPermissions();
    }
    Button loginButton;
    Button clearButton;
    CheckBox rmbrCheckBox;
    EditText userInput;
    EditText passInput;
    TextView registerLabel;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Realm realm;

    public void checkPermissions(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .withListener(new BaseMultiplePermissionsListener()
                {
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        if (report.areAllPermissionsGranted())
                        {
                            initViews();
                        }
                        else
                        {
                            toastRequirePermissions();
                        }
                    }
                }).check();
    }

    public void toastRequirePermissions()
    {
        Toast.makeText(this, "You must provide permissions for app to run", Toast.LENGTH_LONG).show();
        finish();
    }

    public void initViews(){
        // Keep note of the shared pref name used for currently logged in user
        prefs = getSharedPreferences("currentAccount", MODE_PRIVATE);
        editor = prefs.edit();
        loginButton = findViewById(R.id.loginButton);
        clearButton = findViewById(R.id.clearButton);
        rmbrCheckBox = findViewById(R.id.rmbrCheckBox);
        userInput = findViewById(R.id.usernameLoginInput);
        passInput = findViewById(R.id.passwordLoginInput);
        registerLabel = findViewById(R.id.adminLoginLabel);

        loginButton.setOnClickListener(v -> onLoginClick());
        registerLabel.setOnClickListener(v -> onRegisterClick());
        clearButton.setOnClickListener(v -> onClearClick());
        rmbrCheckBox.setOnClickListener(v -> onRmbrClick());

        realm = Realm.getDefaultInstance();

        if (prefs.getBoolean("remember", false)){
            String curr_id = prefs.getString("id", null);
            User curr_user = realm.where(User.class).equalTo("id", curr_id).findFirst();
            userInput.setText(curr_user.getUsername());
            passInput.setText(curr_user.getPassword());
            rmbrCheckBox.setChecked(true);
        } else {
            rmbrCheckBox.setChecked(false);
        }
    }

    public void onLoginClick(){
        Toast toast;
        String username = userInput.getText().toString();
        String password = passInput.getText().toString();
        User userResult = realm.where(User.class).equalTo("username", username).findFirst();

        if (userResult == null){
            toast = Toast.makeText(this, "No user found", Toast.LENGTH_LONG);
            toast.show();
        } else if (!userResult.getPassword().equals(password)) {
            toast = Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_LONG);
            toast.show();
        } else {
            // Passes on the ID of the current user to the currentUser shared pref to be used in the food gallery page
            editor.putString("id", userResult.getId());
            editor.commit();
            Intent intent = new Intent(this, FoodGalleryPage.class);
            startActivity(intent);
        }
    }

    public void onRmbrClick(){
        editor.putBoolean("remember", rmbrCheckBox.isChecked());
        editor.commit();
    }

    public void onClearClick(){
        editor.clear();
        editor.commit();
        Toast toast = Toast.makeText(this, "Cleared data", Toast.LENGTH_LONG);
        toast.show();
    }

    public void onRegisterClick(){
        Intent intent = new Intent(this, AdminPage.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(!realm.isClosed()){
            realm.close();
        }
    }
}