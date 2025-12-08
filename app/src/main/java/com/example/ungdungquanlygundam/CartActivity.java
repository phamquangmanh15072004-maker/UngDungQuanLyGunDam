package com.example.ungdungquanlygundam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.adapter.CartAdapter;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.CartItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartItems;
    private TextView tvTotalPrice, tvEmptyCart;
    private Button btnCheckout;
    private View bottomSummaryLayout;

    private GundamDbHelper dbHelper;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemsList = new ArrayList<>();
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new GundamDbHelper(this);
        android.content.Intent intent = getIntent();
        currentUserId = intent.getIntExtra("LOGGED_IN_USER_ID", -1);
        initViews();
        setupToolbar();
        setupListeners();
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Vui lòng đăng nhập lại", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadCartItems();
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rv_cart_items);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvEmptyCart = findViewById(R.id.tv_empty_cart);
        btnCheckout = findViewById(R.id.btn_checkout);
        bottomSummaryLayout = findViewById(R.id.bottom_summary_layout);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
    }
    // Trong file CartActivity.java
// THAY THẾ TOÀN BỘ phương thức setupListeners

    private void setupListeners() {
        btnCheckout.setOnClickListener(v -> {
            if (cartItemsList == null || cartItemsList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            double totalAmount = 0;
            for (CartItem item : cartItemsList) {
                totalAmount += item.getProduct().getPrice() * item.getQuantity();
            }

            if (currentUserId != -1) {
                ArrayList<CartItem> itemsToCheckout = new ArrayList<>(cartItemsList);

                CheckoutBottomSheetFragment bottomSheet = CheckoutBottomSheetFragment.newInstance(currentUserId, totalAmount, itemsToCheckout, false);

                bottomSheet.setOrderPlacedListener(() -> {
                    dbHelper.clearCart(currentUserId);
                    cartItemsList.clear();
                    cartAdapter.notifyDataSetChanged();
                    updateCartState();
                    Toast.makeText(CartActivity.this, "Đặt hàng thành công! Giỏ hàng đã được dọn dẹp.", Toast.LENGTH_LONG).show();
                    new android.os.Handler().postDelayed(
                            () -> finish(),
                            2000
                    );
                });
                bottomSheet.show(getSupportFragmentManager(), "CheckoutBottomSheet");

            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_cart);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadCartItems() {
        cartItemsList.clear();
        cartItemsList.addAll(dbHelper.getCartItemsByUserId(currentUserId));

        if (cartAdapter == null) {
            cartAdapter = new CartAdapter(this, cartItemsList, this);
            rvCartItems.setAdapter(cartAdapter);
        } else {
            cartAdapter.notifyDataSetChanged();
        }

        updateCartState();
    }

    private void calculateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemsList) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###đ");
        tvTotalPrice.setText(formatter.format(total));
    }

    private void updateCartState() {
        if (cartItemsList.isEmpty()) {
            rvCartItems.setVisibility(View.GONE);
            bottomSummaryLayout.setVisibility(View.GONE);
            tvEmptyCart.setVisibility(View.VISIBLE);
        } else {
            rvCartItems.setVisibility(View.VISIBLE);
            bottomSummaryLayout.setVisibility(View.VISIBLE);
            tvEmptyCart.setVisibility(View.GONE);
            calculateTotalPrice();
        }
    }

    @Override
    public void onQuantityChanged(int cartId, int newQuantity) {
        dbHelper.updateCartItemQuantity(cartId, newQuantity);
        calculateTotalPrice(); // Cập nhật lại tổng tiền ngay lập tức
    }

    // Trong file CartActivity.java
// THAY THẾ TOÀN BỘ phương thức onItemDeleted

    @Override
    public void onItemDeleted(int cartId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có muốn xóa sản phẩm này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    boolean deleted = dbHelper.deleteCartItem(cartId);
                    if (deleted) {
                        // Đảm bảo vị trí hợp lệ trước khi xóa khỏi list
                        if (position >= 0 && position < cartItemsList.size()) {
                            cartItemsList.remove(position);
                            cartAdapter.notifyItemRemoved(position);
                            // Không cần notifyItemRangeChanged ở đây, nó có thể gây lỗi
                        } else {
                            // Nếu vị trí không hợp lệ, tải lại toàn bộ giỏ hàng cho chắc chắn
                            loadCartItems();
                            return;
                        }

                        // Cập nhật lại trạng thái (tổng tiền, giao diện trống...)
                        updateCartState();
                        Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

}
