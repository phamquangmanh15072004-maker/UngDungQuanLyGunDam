package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final Context context;
    private final List<Review> reviewList;
    private int currentUserId = -1;
    private UserActionListener userActionListener;
    private AdminActionListener adminActionListener;
    private  boolean isAdmin;

    public interface UserActionListener {
        void onEditClick(Review review);
        void onDeleteClick(int reviewId);
    }

    public interface AdminActionListener {
        void onReviewLongPressed(Review review);
    }

    public ReviewAdapter(Context context, List<Review> reviewList, AdminActionListener adminListener,boolean isAdmin) {
        this.context = context;
        this.reviewList = reviewList;
        this.adminActionListener = adminListener;
        this.isAdmin = isAdmin;
    }
    public ReviewAdapter(Context context, List<Review> reviewList, int currentUserId, UserActionListener userListener) {
        this.context = context;
        this.reviewList = reviewList;
        this.currentUserId = currentUserId;
        this.userActionListener = userListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUsername.setText(review.getUsername());
        holder.rbRating.setRating(review.getRating());
        holder.tvComment.setText(review.getComment());
        holder.tvDate.setText(review.getReviewDate());
        holder.tvProductName.setText(review.getProductName());
        if (isAdmin) {
            // Nếu là admin, cho layout thông tin sản phẩm HIỆN LÊN
            holder.layoutProductInfo.setVisibility(View.VISIBLE);
        } else {
            // Nếu không phải admin (người dùng thường), cho layout đó ẨN ĐI
            holder.layoutProductInfo.setVisibility(View.GONE);
        }

        // Ẩn/hiện comment
        if (review.getComment() == null || review.getComment().trim().isEmpty()) {
            holder.tvComment.setVisibility(View.GONE);
        } else {
            holder.tvComment.setVisibility(View.VISIBLE);
        }

        // Trường hợp 1: Dành cho User (có UserActionListener và đúng userId)
        if (userActionListener != null && review.getUserId() == currentUserId) {
            holder.layoutUserActions.setVisibility(View.VISIBLE); // Hiển thị layout Sửa/Xóa

            holder.ivEdit.setOnClickListener(v -> userActionListener.onEditClick(review));
            holder.ivDelete.setOnClickListener(v -> userActionListener.onDeleteClick(review.getId()));

            // Vô hiệu hóa long click để tránh xung đột
            holder.itemView.setOnLongClickListener(null);
        }
        // Trường hợp 2: Dành cho Admin (có AdminActionListener)
        else if (adminActionListener != null) {
            holder.layoutUserActions.setVisibility(View.GONE); // Ẩn layout Sửa/Xóa

            holder.itemView.setOnLongClickListener(v -> {
                adminActionListener.onReviewLongPressed(review);
                return true; // Đã xử lý sự kiện
            });
        }
        else {
            holder.layoutUserActions.setVisibility(View.GONE);
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivEdit, ivDelete;
        TextView tvUsername, tvComment, tvDate,tvProductName;
        RatingBar rbRating;
        LinearLayout layoutUserActions;
        LinearLayout layoutProductInfo;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_review_user_avatar);
            tvUsername = itemView.findViewById(R.id.tv_review_username);
            rbRating = itemView.findViewById(R.id.rb_review_rating);
            tvComment = itemView.findViewById(R.id.tv_review_comment);
            tvDate = itemView.findViewById(R.id.tv_review_date);
            layoutUserActions = itemView.findViewById(R.id.layout_user_actions);
            ivEdit = itemView.findViewById(R.id.iv_edit_review);
            ivDelete = itemView.findViewById(R.id.iv_delete_review);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            layoutProductInfo = itemView.findViewById(R.id.layout_product_info);
        }
    }
}
