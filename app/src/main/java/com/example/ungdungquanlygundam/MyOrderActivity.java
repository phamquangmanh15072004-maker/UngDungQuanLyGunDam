package com.example.ungdungquanlygundam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.OrderAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Order;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private GundamDbHelper dbHelper;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order); // Giả sử bạn có layout này

        Intent intent = getIntent();
        currentUserId = intent.getIntExtra("LOGGED_IN_USER_ID", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dbHelper = new GundamDbHelper(this);

        setupToolbar();
        initViews();
        loadOrders();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_my_orders); // ID toolbar trong layout
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đơn Hàng Của Tôi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Xử lý khi nhấn nút back
    }

    private void initViews() {
        rvOrders = findViewById(R.id.rv_my_orders);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        List<Order> loadedOrders = dbHelper.getAllOrdersByUserId(currentUserId);

        if (loadedOrders.isEmpty()) {
            Toast.makeText(this, "Bạn chưa có đơn hàng nào", Toast.LENGTH_SHORT).show();
        }

        orderList.clear();
        orderList.addAll(loadedOrders);
        orderAdapter.notifyDataSetChanged();
    }
}
