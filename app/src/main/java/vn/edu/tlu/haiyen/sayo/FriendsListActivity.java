package vn.edu.tlu.haiyen.sayo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.haiyen.sayo.User;

public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendAdapter friendAdapter;
    private List<User> friendList;
    private FirebaseUser currentUser;
    private DatabaseReference friendsRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_friends_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish()); // Quay lại màn hình trước

        // Khởi tạo
        recyclerView = findViewById(R.id.recyclerViewFriends);
        friendList = new ArrayList<>();
        friendAdapter = new FriendAdapter(this, friendList);
        recyclerView.setAdapter(friendAdapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsRef = FirebaseDatabase.getInstance().getReference("friends");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        if (currentUser != null) {
            loadFriends();
        }
    }

    private void loadFriends() {
        String currentUserId = currentUser.getUid();

        // 1. Lấy danh sách UID của bạn bè
        friendsRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear(); // Xóa danh sách cũ để cập nhật mới
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    String friendId = friendSnapshot.getKey();
                    // 2. Với mỗi UID, lấy thông tin chi tiết của người dùng đó
                    fetchFriendDetails(friendId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }

    private void fetchFriendDetails(String friendId) {
        usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    friendList.add(user);
                    friendAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi
            }
        });
    }
}