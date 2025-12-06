package com.example.ungdungquanlygundam.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private int carID;
    private int userId;
    private int productId;
    private int quantity;
    private Product product;
    private boolean isSelected;

    public CartItem(Product product, int quantity, boolean isSelected) {
        this.product = product;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }
    public CartItem(int cardID, int userId, int productId, int quantity, Product product) {
        this.carID = cardID;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.product = product;
    }
    // ==============================================================

    // --- HÀM CỦA PARCELABLE (ĐÃ SỬA LỖI) ---
    protected CartItem(Parcel in) {
        carID = in.readInt();
        userId = in.readInt();
        productId = in.readInt();
        quantity = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(carID);
        dest.writeInt(userId);
        dest.writeInt(productId);
        dest.writeInt(quantity);
        dest.writeParcelable(product, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };
    public int getCartID() {
        return carID;
    }

    public void setCartID(int id) {
        this.carID = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
