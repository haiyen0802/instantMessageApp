package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    private ImageView imgAvatar;
    private TextView txtName, txtBio;
    private Button btnEdit;

    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private User userProfile; // Lưu thông tin người dùng để truyền đi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ các view từ XML
        imgAvatar = findViewById(R.id.imgAvatar);
        txtName = findViewById(R.id.txtName);
        txtBio = findViewById(R.id.txtBio);
        btnEdit = findViewById(R.id.btnEdit);

        // Lấy thông tin người dùng đang đăng nhập
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để xem thông tin.", Toast.LENGTH_LONG).show();
            // Chuyển về màn hình đăng nhập nếu cần
            // startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Lấy tham chiếu đến node của user trong Database
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Tải thông tin người dùng
        loadUserInfo();

        // Xử lý sự kiện cho nút Edit
        btnEdit.setOnClickListener(v -> {
            if (userProfile != null) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                // Gửi đối tượng User qua cho EditProfileActivity
                intent.putExtra("USER_PROFILE", userProfile);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Dữ liệu chưa được tải xong, vui lòng chờ.", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập các nút điều hướng
        setupNavigationButtons();
    }

    private void loadUserInfo() {
        // Sử dụng addValueEventListener để dữ liệu tự cập nhật khi có thay đổi
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Ánh xạ dữ liệu từ snapshot vào đối tượng User
                    userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        // Cập nhật UI
                        txtName.setText(userProfile.getName());
                        // Kiểm tra nếu bio null hoặc rỗng
                        if (userProfile.getBio() != null && !userProfile.getBio().isEmpty()) {
                            txtBio.setText(userProfile.getBio());
                        } else {
                            txtBio.setText("Chưa có tiểu sử...");
                        }

                        // Dùng Glide để tải ảnh từ URL
                        if (userProfile.getAvatar() != null && !userProfile.getAvatar().isEmpty()) {
                            Glide.with(ProfileActivity.this)
                                    .load(userProfile.getAvatar())
                                    .placeholder(R.drawable.avatar_default) // Ảnh hiển thị trong khi chờ tải
                                    .error(R.drawable.avatar_default) // Ảnh hiển thị khi lỗi
                                    .into(imgAvatar);
                        } else {
                            // Nếu không có avatar, dùng ảnh mặc định
                            imgAvatar.setImageResource(R.drawable.avatar_default);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigationButtons() {
        ImageButton btnMessages = findViewById(R.id.btnMessages);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        btnMessages.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Home_Activity.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> { });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
        });
        RelativeLayout friendRequestsLayout = findViewById(R.id.friend_requests_layout);

        friendRequestsLayout.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, FriendRequestsActivity.class));
        });
    }
}