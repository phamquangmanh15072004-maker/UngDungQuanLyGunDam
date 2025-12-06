package com.example.ungdungquanlygundam;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager; // Import cái này nếu chưa có
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.ReviewAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Review;

import java.util.ArrayList; // Import cái này nếu chưa có
import java.util.List;

// ================== THAY ĐỔI 1: Implement interface của Admin ==================
public class ReviewManagementActivity extends AppCompatActivity implements ReviewAdapter.AdminActionListener {

    private RecyclerView rvReviews;
    private ReviewAdapter adapter;
    private List<Review> reviewList = new ArrayList<>(); // Khởi tạo luôn để tránh lỗi
    private GundamDbHelper dbHelper;
    private TextView tvEmptyReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_management);

        dbHelper = new GundamDbHelper(this);
        initViews();
        setupToolbar();
        loadAllReviews();
    }

    private void initViews() {
        rvReviews = findViewById(R.id.rv_reviews_admin);
        tvEmptyReviews = findViewById(R.id.tv_empty_reviews_admin);
        // Cài đặt LayoutManager ở đây cho chắc chắn
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_review_management);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Đánh Giá"); // Đặt tiêu đề cho rõ ràng
        }
    }

    private void loadAllReviews() {
        List<Review> reviewsFromDb = dbHelper.getAllReviews();
        reviewList.clear(); // Xóa dữ liệu cũ
        if (reviewsFromDb != null) {
            reviewList.addAll(reviewsFromDb);
        }

        if (!reviewList.isEmpty()) {
            rvReviews.setVisibility(View.VISIBLE);
            tvEmptyReviews.setVisibility(View.GONE);

            if (adapter == null) {
                // Khởi tạo adapter lần đầu, gọi constructor dành cho Admin
                adapter = new ReviewAdapter(this, reviewList, this,true);
                rvReviews.setAdapter(adapter);
            } else {
                // Chỉ cần thông báo dữ liệu thay đổi cho những lần tải lại sau
                adapter.notifyDataSetChanged();
            }
        } else {
            rvReviews.setVisibility(View.GONE);
            tvEmptyReviews.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onReviewLongPressed(Review review) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa Đánh Giá")
                .setMessage("Bạn có chắc chắn muốn xóa bình luận của người dùng '" + review.getUsername() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean success = dbHelper.deleteReview(review.getId());
                    if (success) {
                        Toast.makeText(this, "Đã xóa đánh giá.", Toast.LENGTH_SHORT).show();
                        loadAllReviews(); // Tải lại danh sách
                    } else {
                        Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
