package vn.edu.tlu.haiyen.sayo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestsAdapter extends RecyclerView.Adapter<FriendRequestsAdapter.ViewHolder> {

    private Context context;
    private List<User> requestList;
    private DatabaseReference friendReqRef, friendsRef;
    private String currentUserId;

    public FriendRequestsAdapter(Context context, List<User> requestList) {
        this.context = context;
        this.requestList = requestList;
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        friendReqRef = FirebaseDatabase.getInstance().getReference("friend_requests");
        friendsRef = FirebaseDatabase.getInstance().getReference("friends");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = requestList.get(position);
        holder.nameTextView.setText(user.getName());

        // Load avatar bằng Glide
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .into(holder.avatarImageView);

        holder.acceptBtn.setOnClickListener(v -> acceptRequest(user.getUid(), position));
        holder.declineBtn.setOnClickListener(v -> declineRequest(user.getUid(), position));
    }

    private void acceptRequest(String senderId, int position) {
        String timestamp = String.valueOf(System.currentTimeMillis());

        friendsRef.child(currentUserId).child(senderId).setValue(timestamp)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        friendsRef.child(senderId).child(currentUserId).setValue(timestamp);
                        // Xóa lời mời kết bạn
                        friendReqRef.child(currentUserId).child(senderId).removeValue();
                        friendReqRef.child(senderId).child(currentUserId).removeValue();
                        Toast.makeText(context, "Đã đồng ý kết bạn", Toast.LENGTH_SHORT).show();
                        requestList.remove(position);
                        notifyItemRemoved(position);
                    }
                });
    }

    private void declineRequest(String senderId, int position) {
        friendReqRef.child(currentUserId).child(senderId).removeValue();
        friendReqRef.child(senderId).child(currentUserId).removeValue()
                .addOnCompleteListener(task -> {
                    Toast.makeText(context, "Đã từ chối lời mời", Toast.LENGTH_SHORT).show();
                    requestList.remove(position);
                    notifyItemRemoved(position);
                });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        Button acceptBtn, declineBtn;
        CircleImageView avatarImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.request_user_name);
            acceptBtn = itemView.findViewById(R.id.accept_button);
            declineBtn = itemView.findViewById(R.id.decline_button);
            avatarImageView = itemView.findViewById(R.id.request_user_avatar);
        }
    }
}

