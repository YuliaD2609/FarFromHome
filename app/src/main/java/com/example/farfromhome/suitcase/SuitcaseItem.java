package com.example.farfromhome.suitcase;

public class SuitcaseItem {
    private String name;
    private int quantity;
    private String category;

    public SuitcaseItem(String name, int quantity, String category) {
        this.name = name;
        this.quantity = quantity;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        }
    }
}
