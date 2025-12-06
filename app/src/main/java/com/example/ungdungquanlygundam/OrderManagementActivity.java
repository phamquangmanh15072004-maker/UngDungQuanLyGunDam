package com.example.ungdungquanlygundam;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.ungdungquanlygundam.adapter.OrderAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();
    private GundamDbHelper dbHelper;
    private TextView tvEmptyOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        dbHelper = new GundamDbHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar_order_management);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút back
            getSupportActionBar().setTitle("Quản Lý Đơn Hàng");
        }

        rvOrders = findViewById(R.id.rv_orders);
        tvEmptyOrders = findViewById(R.id.tv_empty_orders); // Ánh xạ TextView

        loadOrders();
    }

    private void loadOrders() {
        orderList = dbHelper.getAllOrders();

        if (orderList != null && !orderList.isEmpty()) {
            rvOrders.setVisibility(View.VISIBLE);
            tvEmptyOrders.setVisibility(View.GONE);

            orderAdapter = new OrderAdapter(this, orderList,true);
            rvOrders.setAdapter(orderAdapter);
        } else {
            rvOrders.setVisibility(View.GONE);
            tvEmptyOrders.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
