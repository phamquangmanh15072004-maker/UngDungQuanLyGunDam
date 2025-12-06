package com.example.ungdungquanlygundam.model;

import android.os.Parcel;
import android.os.Parcelable;

// Bỏ `implements Serializable` vì Parcelable đã đủ và tốt hơn.
public class Product implements Parcelable {
    private int id;
    private String name;
    private String description;
    private double price;
    private String imagePath;
    private int stock;
    private String category;
    private String modelPath;

    public Product(){

    }
    public Product(int id, String name, String description, double price, String imagePath , int stock, String category, String modelPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.imagePath = imagePath;
        this.category = category;
        this.modelPath = modelPath;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        stock = in.readInt();
        imagePath = in.readString();
        category = in.readString();
        modelPath = in.readString(); // THÊM DÒNG NÀY: Đọc modelPath từ Parcel
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeInt(stock);
        dest.writeString(imagePath);
        dest.writeString(category);
        dest.writeString(modelPath);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
