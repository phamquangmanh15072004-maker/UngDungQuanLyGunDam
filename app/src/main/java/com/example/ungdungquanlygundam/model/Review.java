package com.example.ungdungquanlygundam.model;

public class Review {
    private int id;
    private int productId;
    private String productName;
    private int userId;
    private float rating;
    private String comment;
    private String reviewDate;

    private String username;


    public Review(int id, int productId, int userId, float rating, String comment, String reviewDate,String productName) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
        this.productName = productName;
    }
    public Review(int id, int productId, int userId, float rating, String comment, String reviewDate) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = reviewDate;
    }

    public int getId() { return id; }
    public int getProductId() { return productId; }
    public int getUserId() { return userId; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getReviewDate() { return reviewDate; }
    public String getUsername() { return username; }
    public  String getProductName() { return productName; }
    public void setUsername(String username) {
        this.username = username;
    }
}
