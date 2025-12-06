package com.example.ungdungquanlygundam.model;

import java.util.List;

public class Order {
    private int id;
    private int userId;
    private double totalAmount;
    private String address;
    private String phone;
    private String status;
    private String orderDate;
    private List<OrderDetail> details;

    public Order(int id, int userId, double totalAmount, String address, String phone, String status, String orderDate) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.address = address;
        this.phone = phone;
        this.status = status;
        this.orderDate = orderDate;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public double getTotalAmount() { return totalAmount; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getOrderDate() { return orderDate; }
    public List<OrderDetail> getDetails() { return details; }
    public void setDetails(List<OrderDetail> details) { this.details = details; }
    public void setStatus(String status) {
        this.status = status;
    }
}
