package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class Home_Activity extends AppCompatActivity {
    // THAY ĐỔI 1: Đổi kiểu dữ liệu và tên biến cho rõ nghĩa
    private List<User> friendList;
    private MessageAdapter friendAdapter; // Tên biến là friendAdapter, nhưng kiểu vẫn là MessageAdapter đã sửa
    private RecyclerView recyclerView;

    private DatabaseReference friendRequestRef;
    private DatabaseReference friendsRef;
    private DatabaseReference usersRef;
    private FirebaseUser currentUser;
    private TextView profileBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ
        profileBadge = findViewById(R.id.profile_badge);
        recyclerView = findViewById(R.id.recyclerViewConversations);
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // THAY ĐỔI 2: Setup RecyclerView với dữ liệu và adapter mới
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendList = new ArrayList<>();
        // Truyền context và danh sách user vào adapter
        friendAdapter = new MessageAdapter(this, friendList);
        recyclerView.setAdapter(friendAdapter);

        if (currentUser != null) {
            String currentUid = currentUser.getUid();
            // Tham chiếu tới các node trên Firebase
            friendRequestRef = FirebaseDatabase.getInstance().getReference("friend_requests").child(currentUid);
            friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUid);
            usersRef = FirebaseDatabase.getInstance().getReference("users");

            listenForFriendRequests();
            loadFriendList(); // THAY ĐỔI 3: Gọi hàm mới để tải bạn bè
        }

        // Các sự kiện click giữ nguyên
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Activity.this, FindFriendsActivity.class);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Activity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Activity.this, SettingActivity.class);
            startActivity(intent);
        });
    }

    // THAY ĐỔI 4: Xóa listener cũ cho "messages" và thay bằng hàm này
    private void loadFriendList() {
        friendsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot friendSnapshot, @Nullable String previousChildName) {
                // Lấy UID của người bạn
                String friendId = friendSnapshot.getKey();
                if (friendId != null) {
                    // Dùng UID đó để lấy thông tin chi tiết từ node "users"
                    usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User friend = userSnapshot.getValue(User.class);
                            if (friend != null) {
                                friend.setUid(userSnapshot.getKey()); // Gán UID cho đối tượng
                                friendList.add(friend);
                                friendAdapter.notifyItemInserted(friendList.size() - 1);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("HomeActivity", "Lỗi khi đọc thông tin bạn bè: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi hủy kết bạn
                String friendIdToRemove = snapshot.getKey();
                for (int i = 0; i < friendList.size(); i++) {
                    if (friendList.get(i).getUid().equals(friendIdToRemove)) {
                        friendList.remove(i);
                        friendAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeActivity", "Lỗi khi đọc danh sách bạn bè: " + error.getMessage());
            }
        });
    }

    private void listenForFriendRequests() {
        // Hàm này giữ nguyên
        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    profileBadge.setVisibility(View.VISIBLE);
                    profileBadge.setText(String.valueOf(snapshot.getChildrenCount()));
                } else {
                    profileBadge.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}