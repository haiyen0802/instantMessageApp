package vn.edu.tlu.haiyen.sayo;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FriendRequestsActivity extends AppCompatActivity {

    private FriendRequestsAdapter adapter;
    private List<User> requestList;

    private DatabaseReference friendRequestsRef;
    private DatabaseReference usersRef;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        // --- BƯỚC 1: ÁNH XẠ VÀ CÀI ĐẶT TOOLBAR ---
        toolbar = findViewById(R.id.toolbar_friend_requests);
        setSupportActionBar(toolbar); // Đặt toolbar làm ActionBar

        // Hiển thị nút quay lại (biểu tượng mũi tên)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.friend_requests_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestList = new ArrayList<>();
        adapter = new FriendRequestsAdapter(this, requestList);
        recyclerView.setAdapter(adapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        friendRequestsRef = FirebaseDatabase.getInstance().getReference("friend_requests").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadFriendRequests();
    }
    // --- BƯỚC 2: XỬ LÝ SỰ KIỆN NHẤN NÚT QUAY LẠI ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Kiểm tra xem item được nhấn có phải là nút "home" không (chính là nút quay lại)
        if (item.getItemId() == android.R.id.home) {
            // Kết thúc (đóng) Activity hiện tại và quay về màn hình trước đó
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFriendRequests() {
        // Luôn bắt đầu bằng việc xóa danh sách cũ để tránh hiển thị trùng lặp
        requestList.clear();
        adapter.notifyDataSetChanged(); // Cập nhật giao diện ngay lập tức để người dùng thấy danh sách trống

        friendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lọc ra các request có type là "received"
                List<String> senderIds = new ArrayList<>();
                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    // Kiểm tra xem node request_type có tồn tại không trước khi lấy giá trị
                    if (requestSnapshot.hasChild("request_type") &&
                            "received".equals(requestSnapshot.child("request_type").getValue(String.class))) {
                        senderIds.add(requestSnapshot.getKey());
                    }
                }

                // Nếu không có lời mời nào, không cần làm gì thêm
                if (senderIds.isEmpty()) {
                    Log.d("FriendRequests", "No new received friend requests.");
                    // Có thể hiển thị một thông báo cho người dùng ở đây (ví dụ: một TextView "Không có lời mời nào")
                    return;
                }

                // Dùng một biến đếm để biết khi nào tất cả các yêu cầu con đã hoàn thành
                final int[] completedRequests = {0};

                // Lặp qua danh sách ID người gửi để lấy thông tin chi tiết
                for (String senderId : senderIds) {
                    if (senderId == null) continue; // Bỏ qua nếu key bị null

                    usersRef.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                user.setUid(userSnapshot.getKey()); // Gán UID cho đối tượng User
                                requestList.add(user);
                            }

                            // Tăng biến đếm sau mỗi lần nhận được kết quả (thành công hoặc không)
                            completedRequests[0]++;

                            // Chỉ cập nhật RecyclerView KHI TẤT CẢ các yêu cầu đã hoàn tất
                            if (completedRequests[0] == senderIds.size()) {
                                Log.d("FriendRequests", "All user data loaded. Updating RecyclerView.");
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("FriendRequests", "Failed to load user data for " + senderId, databaseError.toException());

                            // Vẫn tăng biến đếm để vòng lặp có thể kết thúc
                            completedRequests[0]++;

                            // Kiểm tra xem đây có phải là yêu cầu cuối cùng không
                            if (completedRequests[0] == senderIds.size()) {
                                Log.d("FriendRequests", "All user data loaded (with some errors). Updating RecyclerView.");
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FriendRequests", "Failed to load friend requests.", databaseError.toException());
                // Có thể hiển thị Toast hoặc thông báo lỗi cho người dùng
            }
        });
    }
}

