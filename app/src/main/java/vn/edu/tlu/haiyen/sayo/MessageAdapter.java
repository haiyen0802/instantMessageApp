package vn.edu.tlu.haiyen.sayo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message msg = messageList.get(position);
        holder.tvSenderName.setText(msg.senderName);
        holder.tvText.setText(msg.text);
        holder.tvTimestamp.setText(formatTime(msg.timestamp));

        // Ảnh đại diện tạm thời (sau này bạn dùng Glide load từ URL)
        holder.imgAvatar.setImageResource(R.drawable.avatar_placeholder);

        // Giả lập số tin chưa đọc (bạn có thể lấy thật nếu muốn)
        int unreadCount = 3; // bạn có thể thay bằng msg.unreadCount nếu có field đó

        if (unreadCount > 0) {
            holder.tvUnreadCount.setVisibility(View.VISIBLE);
            holder.tvUnreadCount.setText(String.valueOf(unreadCount));
        } else {
            holder.tvUnreadCount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName, tvText, tvTimestamp;
        ImageView imgAvatar;
        TextView tvUnreadCount;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tvSenderName);
            tvText = itemView.findViewById(R.id.tvText);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvUnreadCount = itemView.findViewById(R.id.tvUnreadCount);
        }
    }

    private String formatTime(long timestamp) {
        return android.text.format.DateFormat.format("HH:mm", timestamp).toString();
    }
}
