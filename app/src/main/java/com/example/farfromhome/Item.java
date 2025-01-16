package com.example.farfromhome;

import java.util.Date;

public class Item {
    private String name;
    private int quantity;
    private Date expiry;
    private String category;

    public Item(String name, int quantity, Date expiry, String category) {
        this.name = name;
        this.quantity = quantity;
        this.expiry = expiry;
        this.category=category;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }

    private boolean marked = false;

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}
