package vn.edu.tlu.haiyen.sayo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Resign_activity extends AppCompatActivity {
    EditText edtNewPassword, edtConfirmPassword;
    Button btnContinue;
    String phone = "";
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resign);

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnContinue = findViewById(R.id.btnContinue);

        // Nhận số điện thoại từ Login_activity
        phone = getIntent().getStringExtra("phone");

        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy tài khoản để đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tham chiếu đến node tài khoản
        userRef = FirebaseDatabase.getInstance().getReference("taikhoan").child(phone);

        // Kiểm tra xem tài khoản có tồn tại trong Firebase không
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(Resign_activity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                    finish(); // Thoát nếu không có tài khoản
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Resign_activity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnContinue.setOnClickListener(v -> {
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật mật khẩu nếu tài khoản hợp lệ
            userRef.child("mk").setValue(newPass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // hoặc chuyển về Login
                } else {
                    Toast.makeText(this, "Lỗi khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}