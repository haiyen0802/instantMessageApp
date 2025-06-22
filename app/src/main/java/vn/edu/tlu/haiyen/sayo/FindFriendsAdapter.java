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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.FindFriendsViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final String currentUserId;
    private final DatabaseReference friendRequestRef;

    public FindFriendsAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");
    }

    @NonNull
    @Override
    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_search, parent, false);
        return new FindFriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position) {
        User user = userList.get(position);

        // Hiển thị thông tin người dùng
        holder.userName.setText(user.getName());
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .into(holder.userAvatar);

        // Xử lý sự kiện cho nút "Kết bạn"
        holder.addFriendButton.setOnClickListener(v -> {
            sendFriendRequest(user.getUid(), holder.addFriendButton);
        });
    }

    // Hàm gửi lời mời kết bạn
    private void sendFriendRequest(String receiverUserId, Button button) {
        friendRequestRef.child(receiverUserId).child(currentUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Vô hiệu hóa nút và đổi text sau khi gửi thành công
                        button.setEnabled(false);
                        button.setText("Đã gửi");
                        Toast.makeText(context, "Đã gửi lời mời kết bạn!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Lớp ViewHolder
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userAvatar;
        TextView userName;
        Button addFriendButton;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar_item);
            userName = itemView.findViewById(R.id.user_name_item);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);
        }
    }
}