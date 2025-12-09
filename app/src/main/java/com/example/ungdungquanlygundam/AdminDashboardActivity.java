package com.example.ungdungquanlygundam;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.MaterialToolbar;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvAdminWelcome;
    private MaterialToolbar toolbar;
    private CardView cardManageProducts, cardManageOrders, cardManageUsers, cardViewRevenue,cardViewReviews;
    private int loggedInUserID  = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        tvAdminWelcome = findViewById(R.id.tvAdminWelcome);
        toolbar = findViewById(R.id.toolbar);
        cardManageProducts = findViewById(R.id.card_manage_products);
        cardManageOrders = findViewById(R.id.card_manage_orders);
        cardManageUsers = findViewById(R.id.card_manage_users);
        cardViewRevenue = findViewById(R.id.card_statistics);
        cardViewReviews = findViewById(R.id.card_review_management);
        setSupportActionBar(toolbar);

        // Nhận và hiển thị lời chào
        Intent intent = getIntent();
        if(intent != null){
            loggedInUserID = intent.getIntExtra("LOGGED_IN_USER_ID", -1);
            String username = intent.getStringExtra("USER_NAME");
            tvAdminWelcome.setText("Chào mừng, " + username + "!");
        }
        setupClickListeners();

    }
    private void setupClickListeners() {// 1. Xử lý khi nhấn vào "Quản lý Sản phẩm"
        cardManageProducts.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ProductManagementActivity.class);
            intent.putExtra("USER_ROLE", 1); // Gửi vai trò Admin
            startActivity(intent);
        });

        // 2. Xử lý khi nhấn vào "Quản lý Đơn hàng"
        cardManageOrders.setOnClickListener(v -> {
            // Tạm thời hiển thị thông báo, sau này sẽ tạo Activity mới
            Intent intent = new Intent(AdminDashboardActivity.this, OrderManagementActivity.class);
            startActivity(intent);
        });

        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        cardViewRevenue.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, RevenueStatsActivity.class);
            startActivity(intent);
        });
        cardViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ReviewManagementActivity.class);
            startActivity(intent);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_dashboard_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_view_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USER_ID", loggedInUserID);
            startActivity(intent);
            Toast.makeText(this, "Mã " + loggedInUserID, Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.menu_logout) {

            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất").setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(R.drawable.logout)
                .show();
    }
}
