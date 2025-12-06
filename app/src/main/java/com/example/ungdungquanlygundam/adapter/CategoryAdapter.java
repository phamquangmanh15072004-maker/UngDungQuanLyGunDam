package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.R;

import java.util.List;

/**
 * Adapter để hiển thị danh sách các danh mục sản phẩm (HG, RG, MG, PG...).
 * Mỗi item trong danh sách là một CardView có icon, tên danh mục và mũi tên.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<String> categoryList;
    private OnItemClickListener listener;

    /**
     * Interface để xử lý sự kiện khi người dùng nhấn vào một item.
     */
    public interface OnItemClickListener {
        void onItemClick(String categoryName);
    }

    /**
     * Phương thức để Activity hoặc Fragment đăng ký lắng nghe sự kiện click.
     * @param listener đối tượng sẽ xử lý sự kiện click.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Constructor của Adapter.
     * @param context Context của Activity/Fragment đang sử dụng Adapter.
     * @param categoryList Danh sách các chuỗi (String) chứa tên danh mục.
     */
    public CategoryAdapter(Context context, List<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo view cho mỗi item từ file layout item_category.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String categoryName = categoryList.get(position);
        holder.tvCategoryName.setText(categoryName);
        switch (categoryName) {
            case "HG":
                holder.ivCategoryIcon.setImageResource(R.drawable.hg);
                break;
            case "RG":
                holder.ivCategoryIcon.setImageResource(R.drawable.rg);
                break;
            case "MG":
                holder.ivCategoryIcon.setImageResource(R.drawable.mg);
                break;
            case "PG":
                holder.ivCategoryIcon.setImageResource(R.drawable.pg);
                break;
            case "SD":
                holder.ivCategoryIcon.setImageResource(R.drawable.sd);
                break;
            default:
                holder.ivCategoryIcon.setImageResource(R.drawable.ic_launcher_background);
                break;
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(categoryName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivCategoryIcon;
        final TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các View từ layout item_category.xml
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}
