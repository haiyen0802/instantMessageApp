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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class onboarding_activity extends AppCompatActivity {
    Button btnnext1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🔒 Kiểm tra nếu đã đăng nhập thì chuyển luôn
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(onboarding_activity.this, Home_Activity.class));
            finish(); // Không quay lại onboarding nữa
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnnext1 = (Button) findViewById(R.id.btnnext1);
        btnnext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(onboarding_activity.this, onboarding2_activity.class);
                startActivity(intent);
            }
        });
    }
}