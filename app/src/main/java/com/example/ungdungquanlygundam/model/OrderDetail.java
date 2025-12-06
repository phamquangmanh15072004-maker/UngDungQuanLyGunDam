package com.example.ungdungquanlygundam.model;

public class OrderDetail {
    private long id;
    private int orderId;
    private int productId;
    private int quantity;
    private double price;

    private Product product;

    public OrderDetail(int productId, String productName, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.product = new Product();
        this.product.setName(productName);
    }

    public OrderDetail(long id,int productId, String productName, int quantity, double price) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.product = new Product();
        this.product.setName(productName);
    }
    public OrderDetail(int id, int orderId, int productId, int quantity, double price, Product product) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.product = product;
    }

    public OrderDetail() {
    }
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
