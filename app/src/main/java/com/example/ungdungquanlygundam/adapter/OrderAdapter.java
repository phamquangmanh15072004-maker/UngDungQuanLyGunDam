package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.content.Intent;import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungquanlygundam.InvoiceActivity;
import com.example.ungdungquanlygundam.R;
import com.example.ungdungquanlygundam.database.GundamDbHelper;
import com.example.ungdungquanlygundam.model.Order;
import com.example.ungdungquanlygundam.model.OrderDetail;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final boolean isAdminMode;
    private final GundamDbHelper dbHelper;

    public OrderAdapter(Context context, List<Order> orderList, boolean isAdminMode) {
        this.context = context;
        this.orderList = orderList;
        this.isAdminMode = isAdminMode;
        this.dbHelper = new GundamDbHelper(context);
    }

    public OrderAdapter(Context context, List<Order> orderList) {
        this(context, orderList, false);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Đơn hàng #" + order.getId());
        holder.tvOrderDate.setText("Ngày đặt: " + formatDisplayDate(order.getOrderDate()));
        holder.tvTotalPrice.setText(new DecimalFormat("###,###,###đ").format(order.getTotalAmount()));
        holder.tvOrderStatus.setText(order.getStatus());
        setStatusColor(holder.tvOrderStatus, order.getStatus());

        List<OrderDetail> details = dbHelper.getOrderDetailsByOrderId(order.getId());
        if (details != null && !details.isEmpty()) {
            holder.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
            OrderDetailAdapter detailAdapter = new OrderDetailAdapter(context, details);
            holder.rvOrderDetails.setAdapter(detailAdapter);
            holder.rvOrderDetails.setVisibility(View.VISIBLE);
        } else {
            holder.rvOrderDetails.setVisibility(View.GONE);
        }
        if (isAdminMode) {
            bindAdminActions(holder, order);
        } else {
            bindUserActions(holder, order);
        }
    }

    private void bindAdminActions(OrderViewHolder holder, Order order) {
        String status = order.getStatus();
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
        holder.btnViewInvoice.setVisibility(View.GONE);
        holder.adminActionsLayout.setVisibility(View.VISIBLE);

        if ("Chờ xác nhận".equalsIgnoreCase(status)) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnConfirm.setOnClickListener(v -> handleConfirmOrder(holder, order));
            holder.btnCancel.setOnClickListener(v -> handleCancelOrder(holder, order));
        } else if (!"Đã hủy".equalsIgnoreCase(status)) {
            holder.btnViewInvoice.setVisibility(View.VISIBLE);
            holder.btnViewInvoice.setOnClickListener(v -> viewInvoice(order.getId()));
        }}


    private void bindUserActions(OrderViewHolder holder, Order order) {
        String status = order.getStatus();
        holder.adminActionsLayout.setVisibility(View.VISIBLE);
        holder.btnConfirm.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);

        if ("Chờ xác nhận".equalsIgnoreCase(status)) {
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(v -> handleCancelOrder(holder, order));
            holder.btnViewInvoice.setVisibility(View.GONE);
        } else {
            holder.btnViewInvoice.setVisibility(View.VISIBLE);
            holder.btnViewInvoice.setOnClickListener(v -> viewInvoice(order.getId()));
        }
    }

    private void handleConfirmOrder(OrderViewHolder holder, Order order) {
        boolean success = dbHelper.updateOrderStatus(order.getId(), "Đã xác nhận");
        if (success) {
            Toast.makeText(context, "Đã xác nhận đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
            order.setStatus("Đã xác nhận");
            notifyItemChanged(holder.getAdapterPosition());
        } else {
            Toast.makeText(context, "Lỗi: Không thể xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCancelOrder(OrderViewHolder holder, Order order) {
        new AlertDialog.Builder(context)
                .setTitle("Xác nhận hủy đơn")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getId() + "? Hành động này sẽ hoàn lại sản phẩm vào kho.")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    List<OrderDetail> itemsInOrder = dbHelper.getOrderDetailsByOrderId(order.getId());
                    if (itemsInOrder != null && !itemsInOrder.isEmpty()) {
                        for (OrderDetail detail : itemsInOrder) {
                            dbHelper.updateProductStock(detail.getProduct().getId(), detail.getQuantity());
                        }
                    } else {
                        Toast.makeText(context, "Không tìm thấy chi tiết đơn hàng.", Toast.LENGTH_SHORT).show();
                    }
                    boolean success = dbHelper.updateOrderStatus(order.getId(), "Đã hủy");
                    if (success) {
                        Toast.makeText(context, "Đã hủy đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
                        order.setStatus("Đã hủy");
                        notifyItemChanged(holder.getAdapterPosition());
                    } else {
                        Toast.makeText(context, "Lỗi: Không thể hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void viewInvoice(int orderId) {
        Intent intent = new Intent(context, InvoiceActivity.class);
        intent.putExtra("ORDER_ID", orderId);
        context.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatDisplayDate(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) return "Không rõ";
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dbFormat.parse(dbDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return displayFormat.format(date);
        } catch (ParseException e) {
            return dbDate;
        }
    }

    private void setStatusColor(TextView textView, String status) {
        if (status == null) {
            textView.setTextColor(Color.BLACK);
            return;
        }
        String colorCode;
        switch (status) {
            case "Chờ xác nhận":
                colorCode = "#FFA500";
                break;
            case "Đã xác nhận":
                colorCode = "#4CAF50";
                break;
            case "Đang giao":
                colorCode = "#2196F3";
                break;
            case "Hoàn thành":
                colorCode = "#008000";
                break;
            case "Đã hủy":
                colorCode = "#F44336";
                break;
            default:
                colorCode = "#000000";
                break;
        }
        textView.setTextColor(Color.parseColor(colorCode));
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvTotalPrice;
        Button btnConfirm, btnCancel;
        RecyclerView rvOrderDetails;
        View adminActionsLayout;
        MaterialButton btnViewInvoice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvTotalPrice = itemView.findViewById(R.id.tv_order_total_price);
            btnConfirm = itemView.findViewById(R.id.btn_confirm_order);
            rvOrderDetails = itemView.findViewById(R.id.rv_order_details);
            btnCancel = itemView.findViewById(R.id.btn_cancel_order);
            adminActionsLayout = itemView.findViewById(R.id.admin_actions_layout);
            btnViewInvoice = itemView.findViewById(R.id.btn_view_invoice);
        }
    }
}
