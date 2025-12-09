package com.example.ungdungquanlygundam;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.CartItem;
import com.example.ungdungquanlygundam.model.Product;
import com.example.ungdungquanlygundam.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckoutBottomSheetFragment extends BottomSheetDialogFragment {
    public interface OrderPlacedListener {
        void onOrderPlaced();
    }

    private OrderPlacedListener orderPlacedListener;

    private int currentUserId;
    private double totalAmount;
    private List<CartItem> cartItems;
    private GundamDbHelper dbHelper;
    private User currentUser;

    private TextView tvCheckoutPhone, tvCheckoutAddress, tvCheckoutTotal;
    private EditText etCheckoutPhone, etCheckoutAddress;
    private LinearLayout layoutInfoDisplay, layoutInfoEdit;
    private Button btnPlaceOrder;
    private boolean isBuyNowMode = false;
    public static CheckoutBottomSheetFragment newInstance(int userId, double totalAmount, List<CartItem> cartItems,boolean isBuyNow) {
        CheckoutBottomSheetFragment fragment = new CheckoutBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        args.putDouble("TOTAL_AMOUNT", totalAmount);
        args.putParcelableArrayList("CART_ITEMS", new ArrayList<>(cartItems));
        args.putBoolean("IS_BUY_NOW", isBuyNow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserId = getArguments().getInt("USER_ID");
            totalAmount = getArguments().getDouble("TOTAL_AMOUNT");
            cartItems = getArguments().getParcelableArrayList("CART_ITEMS");
            isBuyNowMode = getArguments().getBoolean("IS_BUY_NOW", false);
        }
        dbHelper = new GundamDbHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_checkout, container, false);
        initViews(view);
        loadUserData();
        setupListeners();
        return view;
    }

    private void initViews(View view) {
        tvCheckoutPhone = view.findViewById(R.id.tv_checkout_phone);
        tvCheckoutAddress = view.findViewById(R.id.tv_checkout_address);
        tvCheckoutTotal = view.findViewById(R.id.tv_checkout_total);
        etCheckoutPhone = view.findViewById(R.id.et_checkout_phone);
        etCheckoutAddress = view.findViewById(R.id.et_checkout_address);
        layoutInfoDisplay = view.findViewById(R.id.layout_info_display);
        layoutInfoEdit = view.findViewById(R.id.layout_info_edit);
        btnPlaceOrder = view.findViewById(R.id.btn_place_order);
    }

    private void loadUserData() {
        currentUser = dbHelper.getUserById(currentUserId);
        if (currentUser == null) {
            Toast.makeText(getContext(), "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }
        DecimalFormat formatter = new DecimalFormat("###,###,###đ");
        tvCheckoutTotal.setText(formatter.format(totalAmount));

        boolean hasInfo = currentUser.getAddress() != null && !currentUser.getAddress().isEmpty()
                && currentUser.getPhone() != null && !currentUser.getPhone().isEmpty();

        if (hasInfo) {
            layoutInfoDisplay.setVisibility(View.VISIBLE);
            layoutInfoEdit.setVisibility(View.GONE);
            tvCheckoutPhone.setText(currentUser.getPhone());
            tvCheckoutAddress.setText(currentUser.getAddress());
        } else {
            layoutInfoDisplay.setVisibility(View.GONE);
            layoutInfoEdit.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        String phone, address;

        if (layoutInfoEdit.getVisibility() == View.VISIBLE) {phone = etCheckoutPhone.getText().toString().trim();
            address = etCheckoutAddress.getText().toString().trim();

            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ địa chỉ và số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            dbHelper.updateUser(currentUser);
        } else {
            phone = currentUser.getPhone();
            address = currentUser.getAddress();
        }
        for (CartItem item : cartItems) {
            Product productInDb = dbHelper.getProductById(item.getProduct().getId());
            if (productInDb == null || productInDb.getStock() < item.getQuantity()) {
                Toast.makeText(getContext(), "Sản phẩm '" + item.getProduct().getName() + "' không đủ số lượng trong kho.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        boolean success = dbHelper.createOrder(currentUserId, address, phone, cartItems, !isBuyNowMode);

        if (success) {
            for (CartItem item : cartItems) {
                dbHelper.updateProductStock(item.getProduct().getId(), -item.getQuantity());
            }

            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
            if (orderPlacedListener != null) {
                orderPlacedListener.onOrderPlaced();
            }
            dismiss();
        } else {
            Toast.makeText(getContext(), "Đặt hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setOrderPlacedListener(OrderPlacedListener listener) {
        this.orderPlacedListener = listener;
    }
}
