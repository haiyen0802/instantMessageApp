package vn.edu.tlu.haiyen.sayo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingChangePasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnChange;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_change_password);

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnChange = findViewById(R.id.btnChange);

        mAuth = FirebaseAuth.getInstance();

        btnChange.setOnClickListener(v -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
