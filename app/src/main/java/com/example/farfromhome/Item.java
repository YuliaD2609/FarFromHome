package com.example.farfromhome;

import java.util.Date;

public class Item {
    private String name;
    private int quantity;
    private Date expiry;
    private String cathegory;

    public Item(String name, int quantity, Date expiry, String cathegory) {
        this.name = name;
        this.quantity = quantity;
        this.expiry = expiry;
        this.cathegory=cathegory;
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

    public String getCathegory() {
        return cathegory;
    }

    public void setCathegory(String cathegory) {
        this.cathegory = cathegory;
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }


    }
}
