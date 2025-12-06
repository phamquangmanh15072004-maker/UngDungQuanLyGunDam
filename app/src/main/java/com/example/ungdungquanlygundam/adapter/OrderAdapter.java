package com.example.ungdungquanlygundam.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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

    private Context context;
    private List<Order> orderList;
    private boolean isAdminMode;
    public OrderAdapter(Context context, List<Order> orderList, boolean isAdminMode) {
        this.context = context;
        this.orderList = orderList;
        this.isAdminMode = isAdminMode;
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
        Context context = holder.itemView.getContext();

        holder.tvOrderId.setText("Đơn hàng #" + order.getId());
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderDate.setText("Ngày đặt: " + formatDisplayDate(order.getOrderDate()));

        DecimalFormat formatter = new DecimalFormat("###,###,###đ");
        holder.tvTotalPrice.setText(formatter.format(order.getTotalAmount()));

        setStatusColor(holder.tvOrderStatus, order.getStatus());

        String status = order.getStatus();
        if ("Đã xác nhận".equalsIgnoreCase(status) || "Đang giao".equalsIgnoreCase(status) || "Hoàn thành".equalsIgnoreCase(status)) {
            holder.adminActionsLayout.setVisibility(View.VISIBLE);
            holder.btnViewInvoice.setVisibility(View.VISIBLE);

            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            holder.btnViewInvoice.setOnClickListener(v -> {
                Intent intent = new Intent(context, InvoiceActivity.class);
                intent.putExtra("ORDER_ID", order.getId());
                context.startActivity(intent);
            });

        } else {
            holder.btnViewInvoice.setVisibility(View.GONE);
        }

        if (isAdminMode) {
            if ("Chờ xác nhận".equalsIgnoreCase(status)) {
                holder.adminActionsLayout.setVisibility(View.VISIBLE);
                holder.btnConfirm.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnConfirm.setOnClickListener(v -> {
                    GundamDbHelper dbHelper = new GundamDbHelper(context);
                    boolean success = dbHelper.updateOrderStatus(order.getId(), "Đã xác nhận");
                    if (success) {
                        Toast.makeText(context, "Đã xác nhận đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
                        order.setStatus("Đã xác nhận");
                        notifyItemChanged(holder.getAdapterPosition()); // Cập nhật lại item này
                    } else {
                        Toast.makeText(context, "Lỗi: Không thể xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
                    }
                });

                holder.btnCancel.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Xác nhận hủy đơn")
                            .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getId() + "? Hành động này sẽ hoàn lại sản phẩm vào kho.")
                            .setPositiveButton("Đồng ý", (dialog, which) -> {
                                GundamDbHelper dbHelper = new GundamDbHelper(context);
                                List<OrderDetail> itemsInOrder = dbHelper.getOrderDetailsByOrderId(order.getId());
                                if (itemsInOrder == null || itemsInOrder.isEmpty()) {
                                    new AlertDialog.Builder(context)
                                            .setTitle("Lỗi Nghiêm Trọng")
                                            .setMessage("Không thể hủy đơn vì không tìm thấy chi tiết sản phẩm nào trong đơn hàng #" + order.getId() + ". Vui lòng liên hệ quản trị viên.")
                                            .setPositiveButton("OK", null)
                                            .show();
                                    return;
                                }
                                for (OrderDetail detail : itemsInOrder) {
                                    dbHelper.updateProductStock(detail.getProduct().getId(), detail.getQuantity());
                                }

                                boolean success = dbHelper.updateOrderStatus(order.getId(), "Đã hủy");
                                if (success) {
                                    Toast.makeText(context, "Đã hủy đơn hàng #" + order.getId(), Toast.LENGTH_SHORT).show();
                                    order.setStatus("Đã hủy");
                                    notifyItemChanged(holder.getAdapterPosition()); // Cập nhật lại giao diện
                                } else {
                                    Toast.makeText(context, "Lỗi: Không thể hủy đơn hàng.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Không", null)
                            .show();
                });
            }

        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            if (holder.btnViewInvoice.getVisibility() == View.GONE) {
                holder.adminActionsLayout.setVisibility(View.GONE);
            }
        }

        List<OrderDetail> details = order.getDetails();
        if (details != null && !details.isEmpty()) {
            holder.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
            OrderDetailAdapter detailAdapter = new OrderDetailAdapter(context, details);
            holder.rvOrderDetails.setAdapter(detailAdapter);
            holder.rvOrderDetails.setVisibility(View.VISIBLE);
        } else {
            holder.rvOrderDetails.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderStatus, tvOrderDate, tvTotalPrice;
        Button btnConfirm,btnCancel;
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


    private String formatDisplayDate(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) return "";
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = dbFormat.parse(dbDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return displayFormat.format(date);
        } catch (ParseException e) {
            return dbDate;
        }
    }

    private void setStatusColor(TextView textView, String status) {
        if (status == null) return;
        switch (status) {
            case "Chờ xác nhận":
                textView.setTextColor(Color.parseColor("#FFA500"));
                break;
            case "Đã xác nhận":
                textView.setTextColor(Color.parseColor("#4CAF50"));
                break;
            case "Đang giao":
                textView.setTextColor(Color.parseColor("#2196F3"));
                break;
            case "Hoàn thành":
                textView.setTextColor(Color.parseColor("#808080"));
                break;
            case "Đã hủy":
                textView.setTextColor(Color.parseColor("#F44336"));
                break;
            default:
                textView.setTextColor(Color.BLACK);
                break;
        }
    }
}
