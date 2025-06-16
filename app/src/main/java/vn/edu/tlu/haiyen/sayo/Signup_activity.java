package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup_activity extends AppCompatActivity {
    EditText edtFullName, edtPhone, edtPassword, edtConfirmPassword;
    CheckBox chkAgree;
    Button btnCreate;
    TextView txtSignIn ;

    DatabaseReference databaseRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone1);
        edtPassword = findViewById(R.id.edtPassword1);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        chkAgree = findViewById(R.id.chkAgree);
        btnCreate = findViewById(R.id.btnCreate);
        txtSignIn = findViewById(R.id.txtSignIn);

        databaseRef = FirebaseDatabase.getInstance().getReference("taikhoan");

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtFullName.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                String confirm = edtConfirmPassword.getText().toString().trim();

                if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                    Toast.makeText(Signup_activity.this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!pass.equals(confirm)) {
                    Toast.makeText(Signup_activity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!chkAgree.isChecked()) {
                    Toast.makeText(Signup_activity.this, "Vui lòng đồng ý điều khoản", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi dữ liệu lên Firebase (dùng số điện thoại làm key)
                DatabaseReference userRef = databaseRef.child(phone);
                userRef.child("taikhoan").setValue(name);
                userRef.child("mk").setValue(pass);

                Toast.makeText(Signup_activity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                // Xóa form
                edtFullName.setText("");
                edtPhone.setText("");
                edtPassword.setText("");
                edtConfirmPassword.setText("");
                chkAgree.setChecked(false);
            }
        });
        txtSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(Signup_activity.this, Login_activity.class);
            startActivity(intent);
        });
    }
}