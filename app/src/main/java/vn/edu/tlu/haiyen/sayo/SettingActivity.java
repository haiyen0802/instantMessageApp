package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import android.widget.ImageButton;

public class SettingActivity extends AppCompatActivity {

    private Button btnChangePassword, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            // Nếu user chưa đăng nhập, chuyển về màn Login
            Intent intent = new Intent(this, Login_activity.class);
            startActivity(intent);
            finish(); // thoát SettingActivity
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        btnChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingChangePasswordActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login_activity.class);
            startActivity(intent);
            finish();
        });

        // Ánh xạ navbar
        ImageButton btnMessages = findViewById(R.id.btnMessages);
        ImageButton btnProfile = findViewById(R.id.btnProfile);
        ImageButton btnSettings = findViewById(R.id.btnSettings);

        btnMessages.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, Home_Activity.class);
            startActivity(intent);
            finish();
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

        btnSettings.setOnClickListener(v -> {
        });
    }
}
