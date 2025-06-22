package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
    }
}
