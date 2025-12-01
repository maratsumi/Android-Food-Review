package ateneo.food.review;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.realm.Realm;
import io.realm.RealmResults;

public class AdminPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
    }

    RecyclerView userRecyclerView;
    Realm realm;
    Button clearButton;
    Button addButton;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public void initViews(){
        userRecyclerView = findViewById(R.id.userRecyclerView);
        clearButton = findViewById(R.id.userMgtClearButton);
        addButton = findViewById(R.id.userMgtAddButton);
        prefs = getSharedPreferences("accountMgt", MODE_PRIVATE);
        editor = prefs.edit();

        clearButton.setOnClickListener(v -> onClear());
        addButton.setOnClickListener(v -> onAdd());

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        userRecyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getDefaultInstance();

        RealmResults<User> userList = realm.where(User.class).findAll();

        UserAdapter adapter = new UserAdapter(this, userList,true);
        userRecyclerView.setAdapter(adapter);

        builder = new AlertDialog.Builder(this);
    }

    public void onClear(){
        realm.beginTransaction();
        realm.delete(User.class);
        realm.delete(FoodReview.class);
        realm.commitTransaction();
    }
    public void onAdd(){
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }

    public void onDelete(User user){
        builder.setTitle("Are you sure?").setMessage("You are about to delete a user: " + user.getUsername());

        //Confirm action for prompt
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (user.isValid()){
                    realm.beginTransaction();
                    user.deleteFromRealm();
                    realm.commitTransaction();
                }
            }
        });

        //Cancel action for prompt
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    public void onEdit(User user){
        if (user.isValid()){
            editor.putString("id", user.getId());
            editor.commit();
            Intent intent = new Intent(this, EditAccPage.class);
            startActivity(intent);
        }
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