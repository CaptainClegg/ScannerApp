package com.example.misdclegg.scannerapp;

public class FullClass {

    private String serial;
    private String shelf;
    private int quantity;

    public FullClass(String serial, String shelf, int quantity){
        this.serial = serial;
        this.shelf = shelf;
        this.quantity = quantity;
    }

    public String getSerial(){
        return serial;
    }

    public String getShelf(){
        return shelf;
    }

    public int getQuantity(){
        return quantity;
    }

}
