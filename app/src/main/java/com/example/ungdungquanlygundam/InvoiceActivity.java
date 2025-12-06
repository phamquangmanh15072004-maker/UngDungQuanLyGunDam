package com.example.ungdungquanlygundam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Order;
import com.example.ungdungquanlygundam.model.OrderDetail;
import com.example.ungdungquanlygundam.model.User;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    private TextView tvInvoiceId, tvInvoiceDate, tvCustomerName, tvCustomerAddress, tvProductDetails, tvTotalAmount;
    private Button btnShareInvoice;
    private GundamDbHelper dbHelper;
    private int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        dbHelper = new GundamDbHelper(this);
        initViews();

        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy hóa đơn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadInvoiceData();

        btnShareInvoice.setOnClickListener(v -> shareInvoice());
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar_invoice);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvInvoiceId = findViewById(R.id.tv_invoice_id);
        tvInvoiceDate = findViewById(R.id.tv_invoice_date);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerAddress = findViewById(R.id.tv_customer_address);
        tvProductDetails = findViewById(R.id.tv_product_details);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnShareInvoice = findViewById(R.id.btn_share_invoice);
    }

    private void loadInvoiceData() {
        Order order = dbHelper.getOrderById(orderId);
        if (order == null) {
            Toast.makeText(this, "Không thể tải dữ liệu hóa đơn.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = dbHelper.getUserById(order.getUserId());
        String customerName = (user != null) ? user.getUsername() : "Khách lẻ";
        String customerPhone = (user != null) && !user.getPhone().isEmpty() ? user.getPhone() : "Không có";
        String customerInfo = customerName;
        if(!customerPhone.isEmpty()){
            customerInfo += " - " + customerPhone;
        }
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvInvoiceId.setText("Mã HĐ: #" + order.getId());
        tvInvoiceDate.setText("Ngày: " + order.getOrderDate());
        tvCustomerName.setText("Khách hàng: " + customerInfo);
        tvCustomerAddress.setText("Địa chỉ: " + order.getAddress());
        tvTotalAmount.setText(currencyFormat.format(order.getTotalAmount()));
        List<OrderDetail> details = order.getDetails();
        StringBuilder productDetailsText = new StringBuilder();
        if (details != null) {
            for (int i = 0; i < details.size(); i++) {
                OrderDetail detail = details.get(i);
                productDetailsText.append(i + 1)
                        .append(". ")
                        .append(detail.getProduct().getName())
                        .append(" (x")
                        .append(detail.getQuantity())
                        .append(") - ")
                        .append(currencyFormat.format(detail.getPrice() * detail.getQuantity()))
                        .append("\n");
            }
        }
        tvProductDetails.setText(productDetailsText.toString().trim());
    }

    private void shareInvoice() {
        String shareBody = "--- HÓA ĐƠN BÁN HÀNG ---\n" +
                tvInvoiceId.getText().toString() + "\n" +
                tvInvoiceDate.getText().toString() + "\n\n" +
                tvCustomerName.getText().toString() + "\n" +
                tvCustomerAddress.getText().toString() + "\n\n" +
                "Chi tiết sản phẩm:\n" +
                tvProductDetails.getText().toString() + "\n\n" +
                tvTotalAmount.getText().toString() + "\n\n" +
                "Cảm ơn quý khách!";

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hóa Đơn Bán Hàng " + tvInvoiceId.getText().toString());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(sharingIntent, "Chia sẻ hóa đơn qua"));
    }
}
