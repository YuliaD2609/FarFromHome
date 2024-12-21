package com.example.farfromhome.items;

import java.util.Date;
import java.util.GregorianCalendar;

public class Element {
    private String name;
    private String category;
    private GregorianCalendar expireDate;

    public Element(String name, String category, GregorianCalendar expireDate){
        this.name= name;
        this.category=category;
        this.expireDate=expireDate;
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

    public GregorianCalendar getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(GregorianCalendar expireDate) {
        this.expireDate = expireDate;
    }
}
