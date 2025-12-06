package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungquanlygundam.R;

import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImageViewHolder> {

    private Context context;
    private List<Uri> imageUris;
    private OnImageRemoveListener removeListener;

    public interface OnImageRemoveListener {
        void onImageRemoved(int position);
    }

    public ImagePreviewAdapter(Context context, List<Uri> imageUris, OnImageRemoveListener listener) {
        this.context = context;
        this.imageUris = imageUris;
        this.removeListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_preview, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);

        // Dùng Glide để tải ảnh từ Uri hiệu quả
        Glide.with(context)
                .load(imageUri)
                .centerCrop()
                .into(holder.ivPreview);

        holder.btnRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                // --- SỬA LẠI TẠI ĐÂY ---
                // Lấy vị trí một cách an toàn nhất
                int currentPosition = holder.getBindingAdapterPosition();

                // Luôn kiểm tra xem vị trí có hợp lệ không trước khi sử dụng
                if (currentPosition != RecyclerView.NO_POSITION) {
                    removeListener.onImageRemoved(currentPosition);
                }
                // -----------------------
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        ImageView btnRemove;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPreview = itemView.findViewById(R.id.iv_preview);
            btnRemove = itemView.findViewById(R.id.btn_remove_image);
        }
    }
}
