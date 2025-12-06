// File: RevenueStatsActivity.java
package com.example.ungdungquanlygundam;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RevenueStatsActivity extends AppCompatActivity {

    private GundamDbHelper dbHelper;
    private TextView tvTotalRevenue, tvTotalOrders, tvTotalProductsSold,tvTotalProducts,tvTotalInventoryValues,tvTotalProductsTypes;
    private Button btnStartDate, btnEndDate, btnViewAll;
    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private SimpleDateFormat queryFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_stats);

        dbHelper = new GundamDbHelper(this);
        initViews();
        setupToolbar();
        setupListeners();
        updateDateButtons();
        loadStats(queryFormat.format(startDateCalendar.getTime()), queryFormat.format(endDateCalendar.getTime()));
        loadInventoryStats();
    }

    private void initViews() {
        tvTotalRevenue = findViewById(R.id.tv_total_revenue);
        tvTotalOrders = findViewById(R.id.tv_total_orders);
        tvTotalProductsSold = findViewById(R.id.tv_total_products_sold);
        btnStartDate = findViewById(R.id.btn_start_date);
        btnEndDate = findViewById(R.id.btn_end_date);
        btnViewAll = findViewById(R.id.btn_view_all);
        tvTotalProductsTypes = findViewById(R.id.tv_total_product_types);
        tvTotalInventoryValues = findViewById(R.id.tv_total_inventory_value);
        tvTotalProducts = findViewById(R.id.tv_total_inventory_stock);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_stats);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        btnStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        btnEndDate.setOnClickListener(v -> showDatePickerDialog(false));
        btnViewAll.setOnClickListener(v -> loadStats(null, null)); // null để lấy tất cả
    }

    private void showDatePickerDialog(final boolean isStartDate) {
        Calendar calendar = isStartDate ? startDateCalendar : endDateCalendar;
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateDateButtons();
                    loadStats(queryFormat.format(startDateCalendar.getTime()), queryFormat.format(endDateCalendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateButtons() {
        btnStartDate.setText("Từ: " + displayFormat.format(startDateCalendar.getTime()));
        btnEndDate.setText("Đến: " + displayFormat.format(endDateCalendar.getTime()));
    }

    private void loadStats(String startDate, String endDate) {
        Bundle stats = dbHelper.getRevenueStats(startDate, endDate);

        int totalOrders = dbHelper.countConfirmedOrders();
        double totalRevenue = dbHelper.calculateTotalRevenue();
        int totalProductsSold = dbHelper.countTotalProductsSold();
        tvTotalRevenue.setText(String.format("%,.0fđ",totalRevenue));
        tvTotalOrders.setText(String.valueOf(totalOrders));
        tvTotalProductsSold.setText(String.valueOf(totalProductsSold));
    }
    private void loadInventoryStats() {
        int totalProductTypes = dbHelper.getTotalProductTypes();
        double totalInventoryValue = dbHelper.getTotalInventoryValue();
        int totalProducts = dbHelper.getTotalInventoryStock();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvTotalProductsTypes.setText(String.valueOf(totalProductTypes));
        tvTotalInventoryValues.setText(currencyFormat.format(totalInventoryValue));
        tvTotalProducts.setText(String.valueOf(totalProducts));
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
