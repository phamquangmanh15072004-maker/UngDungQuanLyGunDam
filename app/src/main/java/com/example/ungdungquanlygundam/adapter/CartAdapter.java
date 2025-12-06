package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.model.CartItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int cartId, int newQuantity);
        void onItemDeleted(int cartId, int position);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        DecimalFormat formatter = new DecimalFormat("###,###,###đ");

        holder.tvName.setText(item.getProduct().getName());
        holder.tvPrice.setText(formatter.format(item.getProduct().getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        String imagePath = item.getProduct().getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            String firstImageUriString = imagePath.split(",")[0];
            Uri imageUri = Uri.parse(firstImageUriString);
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.products)
                    .error(R.drawable.products)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.products);
        }
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (newQuantity <= item.getProduct().getStock()) {
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(item.getCartID(), newQuantity);
            }
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(item.getCartID(), newQuantity);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onItemDeleted(item.getCartID(), holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // ViewHolder
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvQuantity;
        Button btnIncrease, btnDecrease;
        ImageButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.iv_cart_product_image);
            tvName = itemView.findViewById(R.id.tv_cart_product_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_product_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_cart_increase);
            btnDecrease = itemView.findViewById(R.id.btn_cart_decrease);
            btnDelete = itemView.findViewById(R.id.btn_cart_delete_item);
        }
    }
    public ArrayList<CartItem> getSelectedItems() {
        ArrayList<CartItem> selectedItems = new ArrayList<>();
        // Sửa cartItems thành cartItems vì đó là tên biến trong file của bạn
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    /**
     * Tính và trả về tổng số tiền của các sản phẩm đã được chọn.
     * @return Tổng số tiền (double).
     */
    public double getSelectedTotalAmount() {
        double total = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        return total;
    }
    public void selectAllItems() {
        for (CartItem item : cartItems) {
            item.setSelected(true);
        }
        notifyDataSetChanged();
    }
}
