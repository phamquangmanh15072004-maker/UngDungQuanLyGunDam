package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.model.Product;

import java.util.List;
import java.util.Locale;

public class ProductUserAdapter extends RecyclerView.Adapter<ProductUserAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final OnProductClickListener listener;

    /**
     * Interface này dùng để gửi sự kiện click từ Adapter ra ngoài MainActivity
     */
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    // Constructor để nhận dữ liệu và listener từ MainActivity
    public ProductUserAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view từ file layout item_product_user.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_user, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Lấy sản phẩm hiện tại
        Product product = productList.get(position);
        // Gọi hàm bind để gán dữ liệu và sự kiện
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    /**
     * Hàm này dùng để cập nhật danh sách sản phẩm cho Adapter và
     * thông báo cho RecyclerView để nó vẽ lại giao diện.
     * @param newProductList Danh sách sản phẩm mới từ database.
     */
    public void updateData(List<Product> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    /**
     * Lớp ViewHolder chịu trách nhiệm giữ các View của một item.
     * Bỏ 'static' để có thể truy cập 'context' từ lớp cha nếu cần,
     * nhưng cách tốt nhất là dùng itemView.getContext().
     */
    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            ivProductImage = itemView.findViewById(R.id.iv_product_image_user);
            tvProductName = itemView.findViewById(R.id.tv_product_name_user);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price_user);
        }

        /**
         * Gán dữ liệu của một sản phẩm lên các view và cài đặt listener.
         * @param product Sản phẩm cần hiển thị.
         * @param listener Listener để xử lý sự kiện click.
         */
        public void bind(final Product product, final OnProductClickListener listener) {
            tvProductName.setText(product.getName());

            // Định dạng giá tiền một cách an toàn
            try {
                String formattedPrice = String.format(new Locale("vi", "VN"), "%,.0f đ", product.getPrice());
                tvProductPrice.setText(formattedPrice);
            } catch (Exception e) {
                tvProductPrice.setText("0 đ");
            }

            // --- LOGIC TẢI ẢNH ĐẠI DIỆN ---
            String imagePathString = product.getImagePath();
            if (imagePathString != null && !imagePathString.isEmpty()) {
                // Tách chuỗi bằng dấu phẩy và lấy cái đầu tiên
                String firstImageUriString = imagePathString.split(",")[0];
                Uri imageUri = Uri.parse(firstImageUriString);

                // Dùng Glide để tải ảnh
                Glide.with(itemView.getContext()) // Sử dụng context từ itemView
                        .load(imageUri)
                        .placeholder(R.drawable.products) // Ảnh chờ
                        .error(R.drawable.products)       // Ảnh lỗi
                        .into(ivProductImage);
            } else {
                // Nếu không có ảnh, hiện ảnh mặc định
                ivProductImage.setImageResource(R.drawable.products);
            }

            // Bắt sự kiện click trên toàn bộ item view
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
        }
    }
}
