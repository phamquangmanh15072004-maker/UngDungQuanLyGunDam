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

        // 3. Xử lý khi nhấn vào "Quản lý Người dùng"
        cardManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, UserManagementActivity.class);
            startActivity(intent);
        });

        // 4. Xử lý khi nhấn vào "Xem Doanh thu"
        cardViewRevenue.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, RevenueStatsActivity.class);
            startActivity(intent);
        });
        cardViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ReviewManagementActivity.class);
            startActivity(intent);
        });
    }
    // --- CÁC HÀM XỬ LÝ MENU ---

    // Hàm này để "thổi phồng" (inflate) layout menu của bạn vào Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.admin_dashboard_menu, menu);
        return true;
    }

    // Hàm này được gọi khi người dùng nhấn vào một mục trong menu
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
            // Xử lý khi nhấn "Đăng xuất"
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Hàm thực hiện việc đăng xuất
    private void logout() {
        // Tạo Intent để quay lại màn hình Login
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        // Xóa tất cả các Activity trước đó khỏi stack để người dùng không thể nhấn "Back" quay lại
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng Activity hiện tại
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
    }
}
