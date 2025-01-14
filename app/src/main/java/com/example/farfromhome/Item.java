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
}

