package com.example.mobileproject.models;

public class Order {
    private int id;
    private double total;
    private String date;

    public Order(int id, double total, String date) {
        this.id = id;
        this.total = total;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }

}
