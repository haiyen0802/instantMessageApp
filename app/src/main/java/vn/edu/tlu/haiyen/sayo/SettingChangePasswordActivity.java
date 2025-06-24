package vn.edu.tlu.haiyen.sayo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingChangePasswordActivity extends AppCompatActivity {

    // Thêm edtOldPassword
    private EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChange;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_change_password);

        // Ánh xạ view mới
        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChange = findViewById(R.id.btnChange);

        mAuth = FirebaseAuth.getInstance();

        btnChange.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        // Lấy dữ liệu từ cả 3 trường
        String oldPassword = edtOldPassword.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra trường mật khẩu cũ
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu mới", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Bước 1: Tạo một "giấy chứng nhận" (credential) từ email và mật khẩu CŨ của người dùng
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

            // Bước 2: Yêu cầu người dùng xác thực lại bằng "giấy chứng nhận" đó
            user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
                if (reauthTask.isSuccessful()) {
                    // Xác thực lại thành công! Bây giờ mới được phép đổi mật khẩu.

                    // Bước 3: Cập nhật mật khẩu mới
                    user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {
                            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Lỗi khi cập nhật mật khẩu: " + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    // Xác thực lại thất bại (thường là do nhập sai mật khẩu cũ)
                    Toast.makeText(this, "Xác thực thất bại, mật khẩu hiện tại không đúng.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng hoặc thông tin người dùng bị lỗi.", Toast.LENGTH_SHORT).show();
        }
    }
}