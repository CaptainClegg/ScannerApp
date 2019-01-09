package com.example.misdclegg.scannerapp;

public class WrappingClass {
    private String shelf;
    private int quantity;

    public WrappingClass(String shelf, int quantity){
        this.shelf = shelf;
        this.quantity = quantity;
    }

    public String getShelf(){
        return shelf;
    }

    public int getQuantity(){
        return quantity;
    }
}
