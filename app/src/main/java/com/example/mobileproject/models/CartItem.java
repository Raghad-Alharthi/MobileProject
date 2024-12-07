package com.example.mobileproject.models;

import android.graphics.Bitmap;

public class CartItem {
    private int productId; // ID of the product in the database
    private String name;
    private int quantity;
    private double price;
    private Bitmap image;
    private int availableQuantity; // Maximum quantity available for this item

    // Constructor
    public CartItem(int productId, String name, int quantity, double price, int availableQuantity, Bitmap image) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.image = image;
    }
    public CartItem(int productId, String name, int quantity, double price, Bitmap image) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.image = image;
    }

    // Getter and Setter for availableQuantity
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    // Getter and Setter for productId
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    // Getter and Setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and Setter for quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Getter and Setter for price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getter and Setter for image
    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
