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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsAdapter extends RecyclerView.Adapter<FindFriendsAdapter.FindFriendsViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final String currentUserId;
    private final DatabaseReference friendRequestRef;
    private final DatabaseReference friendsRef;

    public FindFriendsAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        this.friendsRef = FirebaseDatabase.getInstance().getReference().child("friends");
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

        holder.userName.setText(user.getName());
        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .into(holder.userAvatar);

        // QUAN TRỌNG: Kiểm tra và duy trì trạng thái của nút
        maintainButtonState(user.getUid(), holder.addFriendButton);

        // Xử lý sự kiện click
        holder.addFriendButton.setOnClickListener(v -> {
            // Lấy trạng thái hiện tại của nút để quyết định hành động
            String buttonText = holder.addFriendButton.getText().toString();
            if (buttonText.equalsIgnoreCase("Kết bạn")) {
                sendFriendRequest(user.getUid(), holder.addFriendButton);
            } else if ("Đã gửi".equalsIgnoreCase(buttonText)) {
                // Nếu là nút "Đã gửi"
                cancelFriendRequest(user.getUid(), holder.addFriendButton);
            }
        });
    }

    private void maintainButtonState(String otherUserId, Button button) {
        // Mặc định là "Kết bạn"
        button.setText("Kết bạn");
        button.setEnabled(true);

        // 1. Kiểm tra xem có phải đã là bạn bè không?
        friendsRef.child(currentUserId).child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    button.setText("Bạn bè");
                    button.setEnabled(false); // Hoặc chuyển thành nút "Nhắn tin"
                } else {
                    // 2. Nếu chưa phải bạn, kiểm tra trạng thái lời mời
                    friendRequestRef.child(currentUserId).child(otherUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("request_type")) {
                                String requestType = snapshot.child("request_type").getValue(String.class);
                                if ("sent".equals(requestType)) {
                                    button.setText("Đã gửi");
                                    button.setEnabled(true);
                                } else if ("received".equals(requestType)) {
                                    button.setText("Chấp nhận");
                                    button.setEnabled(true);
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Sửa lại hàm này để ghi vào 2 nơi
    private void sendFriendRequest(String receiverUserId, Button button) {
        // Tạo một map để thực hiện nhiều lệnh ghi cùng lúc (atomic update)
        Map<String, Object> requestMap = new HashMap<>();
        // Đường dẫn cho người gửi
        requestMap.put(currentUserId + "/" + receiverUserId + "/request_type", "sent");
        // Đường dẫn cho người nhận
        requestMap.put(receiverUserId + "/" + currentUserId + "/request_type", "received");

        friendRequestRef.updateChildren(requestMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                button.setEnabled(true);
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

    private void cancelFriendRequest(String receiverUserId, Button button) {
        // Tạo một map để xóa dữ liệu ở cả hai nơi cùng lúc
        Map<String, Object> cancelMap = new HashMap<>();
        // Đặt giá trị là null để xóa node của người gửi
        cancelMap.put(currentUserId + "/" + receiverUserId, null);
        // Đặt giá trị là null để xóa node của người nhận
        cancelMap.put(receiverUserId + "/" + currentUserId, null);

        friendRequestRef.updateChildren(cancelMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Kích hoạt lại nút và đổi text về trạng thái ban đầu
                button.setEnabled(true);
                button.setText("Kết bạn");
                Toast.makeText(context, "Đã hủy lời mời kết bạn.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}