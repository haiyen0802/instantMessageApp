package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    private Button btnChangePassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("SettingActivity", "onCreate: Đã vào setting");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            Log.d("SettingActivity", "Đã bấm đăng xuất");

            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login_activity.class);
            startActivity(intent);
            finish();
        });
    }
}
