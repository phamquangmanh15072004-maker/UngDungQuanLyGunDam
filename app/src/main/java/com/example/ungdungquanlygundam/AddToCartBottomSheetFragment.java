package com.example.ungdungquanlygundam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Product;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;

public class AddToCartBottomSheetFragment extends BottomSheetDialogFragment {

    private Product product;
    private int currentUserId;
    private int quantity = 1;

    private ImageView ivProductImage;
    private TextView tvProductPrice, tvProductStock, tvQuantity;
    private Button btnDecrease, btnIncrease, btnAddToCartConfirm;
    private GundamDbHelper dbHelper;


    public static AddToCartBottomSheetFragment newInstance(Product product, int userId) {
        AddToCartBottomSheetFragment fragment = new AddToCartBottomSheetFragment();
        Bundle args = new Bundle();
        args.putParcelable("product", product);
        args.putInt("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }
    public interface OnBuyNowListener {
        void onBuyNowClicked(int quantity);
    }
    private OnBuyNowListener buyNowListener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable("product");
            currentUserId = getArguments().getInt("userId");
        }
        dbHelper = new GundamDbHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_to_cart, container, false);

        // Ánh xạ View
        ivProductImage = view.findViewById(R.id.iv_product_image_sheet);
        tvProductPrice = view.findViewById(R.id.tv_product_price_sheet);
        tvProductStock = view.findViewById(R.id.tv_product_stock_sheet);
        tvQuantity = view.findViewById(R.id.tv_quantity);
        btnDecrease = view.findViewById(R.id.btn_decrease);
        btnIncrease = view.findViewById(R.id.btn_increase);
        btnAddToCartConfirm = view.findViewById(R.id.btn_add_to_cart_confirm);

        // Hiển thị dữ liệu sản phẩm
        if (product != null) {
            DecimalFormat formatter = new DecimalFormat("###,###,###đ");
            tvProductPrice.setText(formatter.format(product.getPrice()));
            tvProductStock.setText("Kho: " + product.getStock());
            tvQuantity.setText(String.valueOf(quantity));
            String[] imagePaths = product.getImagePath().split("\\s*,\\s*");
            if (imagePaths.length > 0 && !imagePaths[0].isEmpty()) {
                Glide.with(this)
                        .load(imagePaths[0])
                        .into(ivProductImage);
            }
        }
        if (getArguments() != null && getArguments().containsKey("BUTTON_TEXT")) {
            String buttonText = getArguments().getString("BUTTON_TEXT");
            btnAddToCartConfirm.setText(buttonText);
        }
        btnIncrease.setOnClickListener(v -> {
            if (quantity < product.getStock()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(getContext(), "Số lượng đã đạt tối đa", Toast.LENGTH_SHORT).show();
            }
        });

        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnAddToCartConfirm.setOnClickListener(v -> {
            if (buyNowListener != null) {
                buyNowListener.onBuyNowClicked(quantity);

            } else {
                if (currentUserId == -1) {
                    Toast.makeText(getContext(), "Vui lòng đăng nhập để thêm vào giỏ", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbHelper.addOrUpdateCartItem(currentUserId, product.getId(), quantity);
                Toast.makeText(getContext(), "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }

            dismiss();
        });

        return view;
    }
    public void setOnBuyNowListener(OnBuyNowListener listener) {
        this.buyNowListener = listener;
    }

    public void setButtonText(String text) {
        if (getArguments() != null) {
            getArguments().putString("BUTTON_TEXT", text);
        }
    }
}
