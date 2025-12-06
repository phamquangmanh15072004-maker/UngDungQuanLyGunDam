package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.R;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageNames;

    public ImageSliderAdapter(Context context, List<String> imageNames) {
        this.context = context;
        this.imageNames = imageNames;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageName = imageNames.get(position);

        // Lấy ID của ảnh từ tên (vì chúng ta đang lưu trong drawable)
        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (imageResId != 0) { // Nếu tìm thấy ảnh
            holder.imageView.setImageResource(imageResId);
        } else {
            // Nếu không tìm thấy, đặt một ảnh lỗi mặc định
            holder.imageView.setImageResource(R.drawable.products); // Bạn cần tạo icon này
        }
    }

    @Override
    public int getItemCount() {
        return imageNames.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_slider_image);
        }
    }
}
