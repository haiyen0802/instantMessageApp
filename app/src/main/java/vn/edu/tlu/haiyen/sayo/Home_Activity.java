package vn.edu.tlu.haiyen.sayo;



import android.content.Intent;
import android.os.Bundle;
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
    private List<Message> messageList;
    private MessageAdapter adapter;
    private DatabaseReference friendRequestRef;
    private FirebaseUser currentUser;
    private TextView profileBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        profileBadge = findViewById(R.id.profile_badge);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            friendRequestRef = FirebaseDatabase.getInstance().getReference("friend_requests").child(currentUser.getUid());
            listenForFriendRequests();
        }

        // Ánh xạ các thành phần
        RecyclerView recyclerView = findViewById(R.id.recyclerViewConversations);
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        ImageButton btnMessages = findViewById(R.id.btnMessages);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        // Kết nối tới Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    adapter.notifyItemInserted(messageList.size() - 1);
                }
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Sự kiện cho nút "+"
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Activity.this, FindFriendsActivity.class);
            startActivity(intent);
        });

        btnMessages.setOnClickListener(v -> { });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Activity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Activity.this, SettingActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void listenForFriendRequests() {
        friendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Đếm số lượng con (là số lời mời)
                    long requestCount = snapshot.getChildrenCount();
                    profileBadge.setVisibility(View.VISIBLE);
                    profileBadge.setText(String.valueOf(requestCount));
                } else {
                    profileBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}
