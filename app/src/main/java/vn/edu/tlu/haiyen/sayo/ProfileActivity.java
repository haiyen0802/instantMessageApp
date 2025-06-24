package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // Các View trong layout
    private ImageView imgAvatar;
    private TextView txtName, txtBio;
    private Button btnEdit;
    private RelativeLayout friendRequestsLayout, friendsListLayout;
    private LinearLayout navbar; // Thêm navbar để ẩn/hiện

    // Firebase
    private DatabaseReference userRef;
    private FirebaseUser loggedInUser; // Người dùng đang đăng nhập

    // Biến để xác định profile đang xem
    private String displayedUserId;
    private User displayedUserProfile; // Lưu thông tin người dùng đang được hiển thị

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Đảm bảo layout của bạn tên là activity_profile.xml

        // Ánh xạ các view từ XML
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtBio = findViewById(R.id.txtBio);
        btnEdit = findViewById(R.id.btnEdit);
        friendRequestsLayout = findViewById(R.id.friend_requests_layout);
        friendsListLayout = findViewById(R.id.friends_list_layout);
        navbar = findViewById(R.id.navbar); // Giả sử navbar của bạn có id là "navbar"

        // Lấy thông tin người dùng đang đăng nhập
        loggedInUser = FirebaseAuth.getInstance().getCurrentUser();
        if (loggedInUser == null) {
            // Xử lý trường hợp người dùng chưa đăng nhập
            Toast.makeText(this, "Bạn cần đăng nhập để sử dụng chức năng này.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        Log.d("ProfileLifecycle", "loggedInUser.getUid(): " + loggedInUser.getUid());
        // === LOGIC QUAN TRỌNG: XÁC ĐỊNH USER ID CẦN HIỂN THỊ ===
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("userId")) {
            Log.d("ProfileLifecycle", "Intent có chứa key 'userId'.");
            displayedUserId = intent.getStringExtra("userId");
            Log.d("ProfileLifecycle", "Giá trị nhận được cho 'userId': " + displayedUserId);

            if (displayedUserId == null || displayedUserId.isEmpty()) {
                Log.e("ProfileLifecycle", "Lỗi: displayedUserId là NULL hoặc RỖNG. Đang đóng Activity.");
                Toast.makeText(this, "Lỗi: ID người dùng không hợp lệ.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            // Trường hợp 2: Xem profile của chính mình
            displayedUserId = loggedInUser.getUid();
        }
        Log.d("ProfileLifecycle", "UserID cuối cùng sẽ được hiển thị: " + displayedUserId);
        // Lấy tham chiếu đến node của user cần hiển thị
        userRef = FirebaseDatabase.getInstance().getReference("users").child(displayedUserId);

        // Cập nhật giao diện dựa trên việc đang xem profile của ai
        updateUIMode();

        // Tải và hiển thị thông tin
        loadUserInfo();

        // Thiết lập sự kiện click
        setupClickListeners();
        Log.d("ProfileLifecycle", "onCreate: Hoàn tất.");
    }

    private void updateUIMode() {
        // So sánh ID người đang xem với ID người đăng nhập
        if (displayedUserId.equals(loggedInUser.getUid())) {
            // Chế độ xem profile của chính mình: HIỆN tất cả
            btnEdit.setVisibility(View.VISIBLE);
            friendRequestsLayout.setVisibility(View.VISIBLE);
            friendsListLayout.setVisibility(View.VISIBLE);
            navbar.setVisibility(View.VISIBLE);
        } else {
            // Chế độ xem profile của người khác: ẨN các mục không cần thiết
            btnEdit.setVisibility(View.GONE);
            friendRequestsLayout.setVisibility(View.GONE);
            friendsListLayout.setVisibility(View.GONE);
            // Có thể thêm nút "Thêm bạn bè" hoặc "Nhắn tin" ở đây nếu muốn
        }
    }

    private void loadUserInfo() {
        // Dùng addValueEventListener để dữ liệu tự cập nhật
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    displayedUserProfile = snapshot.getValue(User.class);
                    if (displayedUserProfile != null) {
                        // Cập nhật UI với dữ liệu mới
                        txtName.setText(displayedUserProfile.getName());

                        if (displayedUserProfile.getBio() != null && !displayedUserProfile.getBio().isEmpty()) {
                            txtBio.setText(displayedUserProfile.getBio());
                        } else {
                            txtBio.setText("Chưa có tiểu sử...");
                        }

                        if (displayedUserProfile.getAvatar() != null && !displayedUserProfile.getAvatar().isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(displayedUserProfile.getAvatar())
                                    .placeholder(R.drawable.avatar_default)
                                    .error(R.drawable.avatar_default)
                                    .into(imgAvatar);
                        } else {
                            imgAvatar.setImageResource(R.drawable.avatar_default);
                        }
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy thông tin người dùng.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileActivity", "Lỗi tải dữ liệu: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Lỗi tải dữ liệu.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        // Nút Edit chỉ hoạt động khi nó visible
        btnEdit.setOnClickListener(v -> {
            if (displayedUserProfile != null) {
                Intent editIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                // Bạn cần làm cho class User có thể "Parcelable" hoặc "Serializable" để gửi cả đối tượng
                // Hoặc đơn giản là không cần gửi gì, EditProfileActivity sẽ tự tải dữ liệu của currentUser
                startActivity(editIntent);
            }
        });

        // Nút xem danh sách bạn bè
        friendsListLayout.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, FriendsListActivity.class));
        });

        // Nút xem lời mời kết bạn
        friendRequestsLayout.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, FriendRequestsActivity.class));
        });

        // Các nút Navbar
        ImageButton btnMessages = findViewById(R.id.btnMessages);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        btnMessages.setOnClickListener(v -> {
            // Chuyển về Home_Activity
            Intent homeIntent = new Intent(ProfileActivity.this, Home_Activity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
        });

        btnSettings.setOnClickListener(v -> {
            // Mở SettingActivity
            startActivity(new Intent(ProfileActivity.this, SettingActivity.class));
        });
    }
}