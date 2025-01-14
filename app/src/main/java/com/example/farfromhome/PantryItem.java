package com.example.farfromhome;

import java.util.Date;

public class PantryItem {
    private String name;
    private int quantity;
    private Integer imageResource;
    private Date expiry;

    public PantryItem(String name, int quantity, Integer imageResource, Date expiry) {
        this.name = name;
        this.quantity = quantity;
        this.imageResource = imageResource;
        this.expiry = expiry;
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

    public Integer getImageResource() {
        return imageResource;
    }

    public void setImageResource(Integer imageResource) {
        this.imageResource = imageResource;
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

    // Decrementa la quantità (minimo 0)
    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
}
