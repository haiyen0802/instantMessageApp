package vn.edu.tlu.haiyen.sayo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // **Thêm thư viện Glide nhé!**
import java.util.List;

// Đổi tên class cho rõ nghĩa (tùy chọn nhưng khuyến khích)
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    // THAY ĐỔI 1: Kiểu dữ liệu của danh sách
    private List<User> userList;
    private Context context;

    // THAY ĐỔI 2: Constructor
    public MessageAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        // THAY ĐỔI 3: Lấy đối tượng User thay vì Message
        User user = userList.get(position);

        // Hiển thị tên bạn bè
        holder.tvSenderName.setText(user.getName());

        // Dùng Glide để load ảnh đại diện (nhớ thêm thư viện Glide vào build.gradle)
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar_placeholder);
        }

        // Xử lý các view không dùng đến trong danh sách bạn bè
        holder.tvText.setText("Bắt đầu trò chuyện"); // Hoặc có thể hiển thị tin nhắn cuối cùng
        holder.tvTimestamp.setVisibility(View.GONE); // Ẩn thời gian
        holder.tvUnreadCount.setVisibility(View.GONE); // Ẩn badge tin chưa đọc

        // THAY ĐỔI 4: Thêm sự kiện click để mở màn hình chat
        holder.itemView.setOnClickListener(v -> {
            // Giả sử bạn có ChatActivity
            Intent intent = new Intent(context, ChatActivity.class);
            // Truyền thông tin của người bạn sang màn hình chat
            intent.putExtra("FRIEND_ID", user.getUid());
            intent.putExtra("FRIEND_NAME", user.getName());
            intent.putExtra("FRIEND_AVATAR", user.getAvatar());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // THAY ĐỔI 5: trả về size của userList
        return userList.size();
    }

    // ViewHolder giữ nguyên, không cần thay đổi
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvText, tvTimestamp, tvUnreadCount;
        ImageView imgAvatar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvText = itemView.findViewById(R.id.tvText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
        }
    }

    // Hàm formatTime không cần nữa, có thể xóa đi
}