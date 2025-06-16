package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login_activity extends AppCompatActivity {
    private EditText edtPhone, edtPassword;
    private Button btnLogin;
    private TextView txtSignup, txtForgot, txtPasswordError;

    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Ánh xạ các thành phần
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);
        txtForgot = findViewById(R.id.txtForgot);
        txtPasswordError = findViewById(R.id.txtPasswordError);

        // Tham chiếu tới "Users" trong Firebase Database
        databaseRef = FirebaseDatabase.getInstance().getReference("taikhoan");

        // Sự kiện nhấn nút Đăng nhập
        btnLogin.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (phone.isEmpty() || password.isEmpty()) {
                showEmptyFieldsDialog();
                return;
            }

            // Kiểm tra tài khoản trên Firebase
            databaseRef.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String correctPassword = snapshot.child("mk").getValue(String.class);
                        if (password.equals(correctPassword)) {
                            Toast.makeText(Login_activity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            txtPasswordError.setVisibility(View.GONE); // Ẩn lỗi

                            // Chuyển sang HomeActivity khi đăng nhập thành công
                            Intent intent = new Intent(Login_activity.this, Home_Activity.class);
                            startActivity(intent);
                            finish(); // Kết thúc Activity hiện tại
                        } else {
                            txtPasswordError.setVisibility(View.VISIBLE); // Hiện lỗi
                        }
                    } else {
                        Toast.makeText(Login_activity.this, "Số điện thoại không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login_activity.this, "Lỗi kết nối: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Sự kiện nhấn liên kết Đăng ký
        txtSignup.setOnClickListener(v -> {
            Intent intent = new Intent(Login_activity.this, Signup_activity.class);
            startActivity(intent);
        });

        // Sự kiện nhấn liên kết Quên mật khẩu
        txtForgot.setOnClickListener(v -> {
            String phone = edtPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(Login_activity.this, "Vui lòng nhập số điện thoại để đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Login_activity.this, Resign_activity.class);
            intent.putExtra("phone", phone); // Truyền số điện thoại sang Resign_activity
            startActivity(intent);
        });
    }

    private void showEmptyFieldsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_empty_fields);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Làm mờ nền
        dialog.getWindow().setDimAmount(0.7f); // Độ mờ, từ 0 đến 1

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setCancelable(false); // Không cho bấm ngoài để tắt
        dialog.show();
    }
}