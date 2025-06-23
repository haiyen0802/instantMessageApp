package vn.edu.tlu.haiyen.sayo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private List<User> friendList;

    public FriendAdapter(Context context, List<User> friendList) {
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        User user = friendList.get(position);
        holder.txtName.setText(user.getName());

        // Dùng Glide để tải ảnh
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(context)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.avatar_default) // Ảnh chờ
                    .error(R.drawable.avatar_default) // Ảnh lỗi
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.avatar_default);
        }
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgFriendAvatar);
            txtName = itemView.findViewById(R.id.txtFriendName);
        }
    }
}