package vn.edu.tlu.haiyen.sayo;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private String friendId;
    private String friendName;
    private String currentUserId;
    private String chatRoomId;

    private DatabaseReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Lấy thông tin từ Intent
        friendId = getIntent().getStringExtra("FRIEND_ID");
        friendName = getIntent().getStringExtra("FRIEND_NAME");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(friendName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
        toolbar.setNavigationOnClickListener(v -> finish());

        // Ánh xạ views
        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // Setup RecyclerView
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Tin nhắn mới nhất ở dưới
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        // Tạo ID phòng chat duy nhất
        chatRoomId = createChatRoomId(currentUserId, friendId);
        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatRoomId);

        // Thiết lập sự kiện gửi tin nhắn
        buttonSend.setOnClickListener(v -> sendMessage());

        // Lắng nghe và tải tin nhắn
        listenForMessages();
    }

    private String createChatRoomId(String uid1, String uid2) {
        // Sắp xếp UID theo thứ tự chữ cái để đảm bảo ID là duy nhất và nhất quán
        List<String> ids = Arrays.asList(uid1, uid2);
        Collections.sort(ids);
        return ids.get(0) + "_" + ids.get(1);
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(messageText)) {
            long timestamp = System.currentTimeMillis();
            ChatMessage chatMessage = new ChatMessage(currentUserId, friendId, messageText, timestamp);

            // Đẩy tin nhắn lên Firebase
            chatRef.push().setValue(chatMessage)
                    .addOnSuccessListener(aVoid -> {
                        // Xóa text trong ô input sau khi gửi thành công
                        editTextMessage.setText("");
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi nếu không gửi được
                        Log.e("ChatActivity", "Gửi tin nhắn thất bại", e);
                    });
        }
    }

    private void listenForMessages() {
        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage message = snapshot.getValue(ChatMessage.class);
                if (message != null) {
                    messageList.add(message);
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1); // Cuộn xuống tin nhắn mới nhất
                }
            }
            // Các phương thức khác có thể để trống
            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}