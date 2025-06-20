package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup_activity extends AppCompatActivity {

    EditText edtFullName, edtPhone, edtEmail, edtPassword, edtConfirmPassword;
    CheckBox chkAgree;
    Button btnCreate;
    TextView txtSignIn;

    FirebaseAuth mAuth;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone1);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword1);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        chkAgree = findViewById(R.id.chkAgree);
        btnCreate = findViewById(R.id.btnCreate);
        txtSignIn = findViewById(R.id.txtSignIn);

        btnCreate.setOnClickListener(v -> {
            String name = edtFullName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^\\d{9,10}$")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!chkAgree.isChecked()) {
                Toast.makeText(this, "Vui lòng đồng ý điều khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng ký Firebase Auth bằng Email
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String uid = firebaseUser.getUid();

                                // Lưu thông tin người dùng
                                userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                userRef.child("name").setValue(name);
                                userRef.child("phone").setValue(phone);
                                userRef.child("email").setValue(email);
                                userRef.child("avatar").setValue("");

                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                // Reset form
                                edtFullName.setText("");
                                edtPhone.setText("");
                                edtEmail.setText("");
                                edtPassword.setText("");
                                edtConfirmPassword.setText("");
                                chkAgree.setChecked(false);

                                startActivity(new Intent(this, Login_activity.class));
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(Signup_activity.this, Login_activity.class));
        });
    }
}
