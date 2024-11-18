package com.example.mobileproject.models;

public class CartItem {
    private String name;
    private String size;
    private int quantity;
    private double price;
    private int imageResId;

    public CartItem(String name, String size, int quantity, double price, int imageResId) {
        this.name = name;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
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

    public int getImageResId() {
        return imageResId;
    }
}
