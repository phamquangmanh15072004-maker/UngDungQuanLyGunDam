package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.model.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    public interface OnProductListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }
    private OnProductListener listener;
    public ProductAdapter(Context context, List<Product> productList,OnProductListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }
    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(formatter.format(product.getPrice()));
        String imagePath = product.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            String firstImageUriString = imagePath.split(",")[0];
            Uri imageUri = Uri.parse(firstImageUriString);
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.products)
                    .error(R.drawable.products)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.products);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(product);
            }
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {listener.onDeleteClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (productList != null) {
            return productList.size();
        }
        return 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice;
        TextView btnEdit, btnDelete; // Thay vì Button, ta dùng TextView có drawable
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            btnEdit = itemView.findViewById(R.id.btn_edit_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_product);
        }
    }
}
