package vn.edu.tlu.haiyen.sayo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatarEdit;
    private EditText etNameEdit, etBioEdit;
    private Button btnSave;
    private ProgressBar progressBarEdit;

    private User currentUserProfile;
    private Uri newImageUri; // Lưu URI của ảnh mới được chọn

    private DatabaseReference userRef;
    private StorageReference storageRef;

    // ActivityResultLauncher để chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    newImageUri = result.getData().getData();
                    // Hiển thị ảnh vừa chọn
                    Glide.with(this).load(newImageUri).into(imgAvatarEdit);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Lấy dữ liệu user đã gửi từ ProfileActivity
        currentUserProfile = (User) getIntent().getSerializableExtra("USER_PROFILE");
        if (currentUserProfile == null) {
            Toast.makeText(this, "Lỗi: Không nhận được dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi tạo Firebase
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("avatars").child(userId);

        // Ánh xạ views
        imgAvatarEdit = findViewById(R.id.imgAvatarEdit);
        etNameEdit = findViewById(R.id.etNameEdit);
        etBioEdit = findViewById(R.id.etBioEdit);
        btnSave = findViewById(R.id.btnSave);
        progressBarEdit = findViewById(R.id.progressBarEdit);

        // Điền thông tin hiện tại vào các trường
        etNameEdit.setText(currentUserProfile.getName());
        etBioEdit.setText(currentUserProfile.getBio());
        Glide.with(this)
                .load(currentUserProfile.getAvatar())
                .placeholder(R.drawable.avatar_default)
                .into(imgAvatarEdit);

        // Sự kiện click để chọn ảnh mới
        imgAvatarEdit.setOnClickListener(v -> openGallery());

        // Sự kiện click nút Lưu
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }



    private void saveChanges() {
        String newName = etNameEdit.getText().toString().trim();
        String newBio = etBioEdit.getText().toString().trim();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "LỖI: Người dùng chưa đăng nhập! Không thể tải lên.", Toast.LENGTH_LONG).show();
            Log.e("UploadError", "currentUser is null. Cannot upload.");
            setLoading(false); // Dừng ProgressBar nếu có
            return; // Dừng hàm tại đây, không làm gì nữa
        }

        if (newName.isEmpty()) {
            etNameEdit.setError("Tên không được để trống!");
            etNameEdit.requestFocus();
            return;
        }

        setLoading(true);

        // Nếu người dùng chọn ảnh mới
        if (newImageUri != null) {
            // Tải ảnh lên Storage, sau đó cập nhật thông tin
            uploadAvatarAndSaveData(newName, newBio);
        } else {
            // Nếu không đổi ảnh, chỉ cập nhật thông tin text
            updateDataToDatabase(newName, newBio, currentUserProfile.getAvatar());
        }
    }

    private void uploadAvatarAndSaveData(String name, String bio) {

        Log.d("UploadPath", "Đường dẫn upload: " + storageRef.getPath());
        storageRef.putFile(newImageUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String avatarUrl = uri.toString();
                            updateDataToDatabase(name, bio, avatarUrl);
                        })
                        .addOnFailureListener(e -> {
                            setLoading(false);
                            Toast.makeText(this, "Lỗi lấy URL ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Lỗi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateDataToDatabase(String name, String bio, String avatarUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("bio", bio);
        updates.put("avatar", avatarUrl);

        userRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish(); // Quay lại ProfileActivity
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBarEdit.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
        } else {
            progressBarEdit.setVisibility(View.GONE);
            btnSave.setEnabled(true);
        }
    }
}