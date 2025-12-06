package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.net.Uri; // <<--- IMPORT LẠI DÒNG NÀY
import android.util.Log; // Giữ lại để debug nếu cần
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungquanlygundam.R;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private Context context;
    // Tên biến bây giờ là danh sách các chuỗi Uri
    private List<String> imageUriStringList;

    public ProductImageAdapter(Context context, List<String> imageUriStringList) {
        this.context = context;
        this.imageUriStringList = imageUriStringList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Lấy chuỗi Uri từ danh sách
        String uriString = imageUriStringList.get(position);

        // ================== LOGIC ĐÚNG ĐỂ TẢI ẢNH TỪ BỘ NHỚ ==================
        try {
            // 1. Chuyển đổi chuỗi String trở lại thành đối tượng Uri
            Uri imageUri = Uri.parse(uriString);

            // 2. Dùng Glide để tải ảnh từ Uri
            Glide.with(context)
                    .load(imageUri) // Tải bằng Uri
                    .placeholder(R.drawable.products) // Ảnh chờ
                    .error(R.drawable.products)       // Ảnh lỗi (quan trọng!)
                    .into(holder.imageView);

        } catch (Exception e) {
            // Bắt các lỗi có thể xảy ra (ví dụ: Uri không hợp lệ, không có quyền truy cập)
            Log.e("ImageAdapterError", "Lỗi khi tải ảnh: " + uriString, e);
            // Nếu có lỗi, hiển thị ảnh mặc định
            holder.imageView.setImageResource(R.drawable.products);
        }
        // =======================================================================
    }

    @Override
    public int getItemCount() {
        // Nếu danh sách null thì trả về 0 để tránh crash
        return imageUriStringList != null ? imageUriStringList.size() : 0;
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            // Hãy chắc chắn ID này khớp với file item_image_slider.xml
            imageView = itemView.findViewById(R.id.iv_slider_image);
        }
    }
}
