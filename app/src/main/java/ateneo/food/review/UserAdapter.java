package ateneo.food.review;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserAdapter extends RealmRecyclerViewAdapter<User, UserAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userPhoto;
        TextView userNameLabel;
        TextView userPassLabel;
        Button editButton;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameLabel = itemView.findViewById(R.id.userMgtNameLabel);
            userPassLabel = itemView.findViewById(R.id.userMgtPassLabel);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            userPhoto = itemView.findViewById(R.id.userMgtProfileImage);
        }
    }

    AdminPage activity;

    public UserAdapter(AdminPage activity, @Nullable OrderedRealmCollection<User> data, boolean autoUpdate) {
        super(data, autoUpdate);
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = activity.getLayoutInflater().inflate(R.layout.usermgt_row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = getItem(position);

        holder.userNameLabel.setText(user.getUsername());
        holder.userPassLabel.setText(user.getPassword());

        holder.editButton.setOnClickListener(v -> onEditClick(user));
        holder.deleteButton.setOnClickListener(v -> onDeleteClick(user));

        File cacheDir = activity.getExternalCacheDir();
        File photo = new File(cacheDir, user.getId()+".jpeg");
        if (photo.exists()){
            Picasso.get()
                    .load(photo)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(holder.userPhoto);
        }
    }
    public void onDeleteClick(User user){
        activity.onDelete(user);
    }
    public void onEditClick(User user){
        activity.onEdit(user);
    }
}
