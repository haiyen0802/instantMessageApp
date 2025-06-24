package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText; // Thêm import này
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Thêm import này nếu dùng Java 8+

public class Home_Activity extends AppCompatActivity {

    // === BẮT ĐẦU PHẦN THAY ĐỔI 1 ===
    private List<User> masterFriendList; // Danh sách chứa TẤT CẢ bạn bè
    private List<User> displayedFriendList; // Danh sách được lọc để HIỂN THỊ
    private MessageAdapter friendAdapter;
    private RecyclerView recyclerView;
    private TextInputEditText edtSearch; // Thêm biến cho ô tìm kiếm
    // === KẾT THÚC PHẦN THAY ĐỔI 1 ===

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
        edtSearch = findViewById(R.id.edtSearch); // Ánh xạ ô tìm kiếm

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // === BẮT ĐẦU PHẦN THAY ĐỔI 2 ===
        // Setup RecyclerView với dữ liệu và adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        masterFriendList = new ArrayList<>();
        displayedFriendList = new ArrayList<>();
        // Adapter sẽ làm việc với `displayedFriendList`
        friendAdapter = new MessageAdapter(this, displayedFriendList);
        recyclerView.setAdapter(friendAdapter);
        // === KẾT THÚC PHẦN THAY ĐỔI 2 ===

        if (currentUser != null) {
            String currentUid = currentUser.getUid();
            // Tham chiếu tới các node trên Firebase
            friendRequestRef = FirebaseDatabase.getInstance().getReference("friend_requests").child(currentUid);
            friendsRef = FirebaseDatabase.getInstance().getReference("friends").child(currentUid);
            usersRef = FirebaseDatabase.getInstance().getReference("users");

            listenForFriendRequests();
            loadFriendList();
        }

        setupSearchListener(); // Gọi hàm cài đặt lắng nghe tìm kiếm

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

    // === BẮT ĐẦU PHẦN THAY ĐỔI 3: THÊM HÀM TÌM KIẾM ===
    private void setupSearchListener() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì ở đây
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lọc danh sách ngay khi người dùng gõ
                filterFriends(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì ở đây
            }
        });
    }

    private void filterFriends(String query) {
        // Xóa danh sách hiển thị hiện tại
        displayedFriendList.clear();

        // Nếu ô tìm kiếm trống, hiển thị tất cả bạn bè
        if (query.isEmpty()) {
            displayedFriendList.addAll(masterFriendList);
        } else {
            // Nếu có từ khóa, lọc từ danh sách master
            // Chuyển cả tên người dùng và từ khóa về chữ thường để tìm kiếm không phân biệt hoa/thường
            String lowerCaseQuery = query.toLowerCase();
            for (User friend : masterFriendList) {
                // Giả sử lớp User của bạn có phương thức getName()
                if (friend.getName().toLowerCase().contains(lowerCaseQuery)) {
                    displayedFriendList.add(friend);
                }
            }
        }
        // Thông báo cho adapter rằng dữ liệu đã thay đổi hoàn toàn
        friendAdapter.notifyDataSetChanged();
    }
    // === KẾT THÚC PHẦN THAY ĐỔI 3 ===


    private void loadFriendList() {
        friendsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot friendSnapshot, @Nullable String previousChildName) {
                String friendId = friendSnapshot.getKey();
                if (friendId != null) {
                    usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User friend = userSnapshot.getValue(User.class);
                            if (friend != null) {
                                friend.setUid(userSnapshot.getKey());
                                // === BẮT ĐẦU PHẦN THAY ĐỔI 4 ===
                                // Thêm vào danh sách master
                                masterFriendList.add(friend);
                                // Lọc lại danh sách hiển thị để cập nhật giao diện
                                // (quan trọng khi đang tìm kiếm mà có bạn mới được thêm vào)
                                filterFriends(edtSearch.getText().toString());
                                // === KẾT THÚC PHẦN THAY ĐỔI 4 ===
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
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Nếu thông tin bạn bè thay đổi (ví dụ: đổi tên), bạn cần cập nhật lại
                // Tạm thời bỏ qua, nhưng có thể thêm logic ở đây sau
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String friendIdToRemove = snapshot.getKey();
                if (friendIdToRemove == null) return;

                // === BẮT ĐẦU PHẦN THAY ĐỔI 5 ===
                // Xóa khỏi danh sách master
                for (int i = 0; i < masterFriendList.size(); i++) {
                    if (masterFriendList.get(i).getUid().equals(friendIdToRemove)) {
                        masterFriendList.remove(i);
                        break;
                    }
                }
                // Lọc lại danh sách hiển thị để cập nhật giao diện
                filterFriends(edtSearch.getText().toString());
                // === KẾT THÚC PHẦN THAY ĐỔI 5 ===
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