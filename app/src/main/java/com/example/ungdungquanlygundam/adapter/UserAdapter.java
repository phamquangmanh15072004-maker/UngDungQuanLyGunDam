package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private OnUserClickListener clickListener;
    private OnUserLongClickListener longListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }
    public interface OnUserLongClickListener {
        void onUserLongClick(User user);
    }
    public UserAdapter(Context context, List<User> userList, OnUserClickListener clickListener) {
        this.context = context;
        this.userList = userList;
        this.clickListener = clickListener;
    }
    public UserAdapter(Context context, List<User> userList, OnUserClickListener clickListener,OnUserLongClickListener longListener) {
        this.context = context;
        this.userList = userList;
        this.clickListener = clickListener;
        this.longListener = longListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUsername.setText(user.getUsername());
        holder.tvEmail.setText(user.getPassword());
        if (user.getRole() == 1) {
            holder.tvRole.setText("Admin");
            holder.tvRole.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_role_admin));
        } else {
            holder.tvRole.setText("User");
            holder.tvRole.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_role_user));
        }
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserClick(user);
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longListener != null) {
                longListener.onUserLongClick(user);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvEmail, tvRole;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvEmail = itemView.findViewById(R.id.tv_user_email);
            tvRole = itemView.findViewById(R.id.tv_user_role);
        }
    }
}
