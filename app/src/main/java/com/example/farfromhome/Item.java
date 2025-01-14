package com.example.farfromhome;

public class Item {
    private String name;
    private int quantity;
    private Integer imageResource;

    public Item(String name, int quantity, Integer imageResource) {
        this.name = name;
        this.quantity = quantity;
        this.imageResource = imageResource;
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

