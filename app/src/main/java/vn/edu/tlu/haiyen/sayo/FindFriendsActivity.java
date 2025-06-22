package vn.edu.tlu.haiyen.sayo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FindFriendsActivity extends AppCompatActivity {

    private FindFriendsAdapter findFriendsAdapter;
    private List<User> userList;
    private DatabaseReference usersRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        // Khởi tạo các thành phần
        Toolbar toolbar = findViewById(R.id.toolbar_find_friends);
        TextInputEditText searchBox = findViewById(R.id.search_users_box);
        RecyclerView recyclerView = findViewById(R.id.find_friends_recycler_view);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUserID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        findFriendsAdapter = new FindFriendsAdapter(this, userList);
        recyclerView.setAdapter(findFriendsAdapter);

        // Thiết lập Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Thêm listener cho thanh tìm kiếm
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi người dùng gõ chữ, bắt đầu tìm kiếm
                searchForUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm thực hiện query lên Firebase
    @SuppressLint("NotifyDataSetChanged")
    private void searchForUsers(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            userList.clear();
            if (findFriendsAdapter != null) {
                findFriendsAdapter.notifyDataSetChanged();
            }
            return;
        }

        // Tạo query để tìm user có trường "phone" khớp chính xác với phoneNumber
        Query searchQuery = usersRef.orderByChild("phone").equalTo(phoneNumber);

        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() { // Dùng addListenerForSingleValueEvent vì chỉ cần tìm 1 lần
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Xóa kết quả cũ
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.setUid(snapshot.getKey());

                            // Không hiển thị chính mình trong kết quả tìm kiếm
                            if (!user.getUid().equals(currentUserID)) {
                                userList.add(user);
                            }
                        }
                    }
                } else {
                   // Toast.makeText(FindFriendsActivity.this, "Không tìm thấy người dùng với SĐT này.", Toast.LENGTH_SHORT).show();
                }
                // Cập nhật lại RecyclerView
                findFriendsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FindFriendsActivity.this, "Lỗi tìm kiếm: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}