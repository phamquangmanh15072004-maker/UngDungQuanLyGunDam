package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.model.OrderDetail;

import java.text.DecimalFormat;
import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.DetailViewHolder> {
    private List<OrderDetail> detailList;
    private Context context;

    public OrderDetailAdapter(Context context, List<OrderDetail> detailList) {
        this.context = context;
        this.detailList = detailList;
    }

    @NonNull @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_details, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        OrderDetail detail = detailList.get(position);
        holder.tvProductName.setText(detail.getProduct().getName());
        holder.tvQuantity.setText("x " + detail.getQuantity());

        DecimalFormat formatter = new DecimalFormat("###,###,###đ");
        holder.tvPrice.setText(formatter.format(detail.getPrice()));
    }

    @Override public int getItemCount() { return detailList.size(); }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;
        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_detail_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_detail_quantity);
            tvPrice = itemView.findViewById(R.id.tv_detail_price);
        }
    }
}
