package com.example.mobileproject.models;

public class Plant {
    private int id;
    private String name;
    private String category;
    private String description;
    private double price;
    private int quantityAvailable;
    private byte[] image;

    // Full Constructor
    public Plant(int id, String name, String category, String description, double price, int quantityAvailable, byte[] image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
        this.image = image;
    }

    // Default Constructor
    public Plant() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public byte[] getImageBlob() { // Renamed getter for consistency
        return image;
    }

    public void setImageBlob(byte[] image) { // Renamed setter for consistency
        this.image = image;
    }

    // Debugging Helper
    @Override
    public String toString() {
        return "Plant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantityAvailable=" + quantityAvailable +
                '}';
    }
}
