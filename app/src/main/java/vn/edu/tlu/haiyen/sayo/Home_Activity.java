package vn.edu.tlu.haiyen.sayo;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class Home_Activity extends AppCompatActivity {
    private ListView messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Ánh xạ các thành phần
        messageList = findViewById(R.id.messageList);
        ImageButton btnAdd = findViewById(R.id.btnAdd);
        ImageButton btnMessages = findViewById(R.id.btnMessages);
        ImageButton btnProfile = findViewById(R.id.btnProfile);

        // TODO: Thêm adapter cho ListView để hiển thị danh sách tin nhắn từ Firebase
        // Ví dụ:
        // String[] messages = {"Alex Anderson: How are you today?", ...};
        // ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
        // messageList.setAdapter(adapter);

        // Sự kiện cho nút "+"
        btnAdd.setOnClickListener(v -> {
            // TODO: Xử lý thêm tin nhắn mới
        });

        // Sự kiện cho nút tin nhắn
        btnMessages.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình tin nhắn
        });

        // Sự kiện cho nút người dùng
        btnProfile.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình hồ sơ
        });
    }
}