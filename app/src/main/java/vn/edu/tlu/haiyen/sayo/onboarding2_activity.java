package vn.edu.tlu.haiyen.sayo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class onboarding2_activity extends AppCompatActivity {
    Button btnLogin, btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding2);
        btnLogin = findViewById(R.id.btnNext3);
        btnSkip = findViewById(R.id.btnSkip3);

        btnLogin.setOnClickListener(v -> {
            // Tạm thời quay về màn hình onboarding 1, bạn thay bằng LoginActivity sau
            Intent intent = new Intent(onboarding2_activity.this, onboarding3_activity.class);
            startActivity(intent);
        });

        btnSkip.setOnClickListener(v -> {
            Intent intent = new Intent(onboarding2_activity.this, onboarding_activity.class);
            startActivity(intent);
        });
    }
}