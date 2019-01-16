package com.example.misdclegg.scannerapp;

public class ScheduleClass {
    private String sequence;
    private String workOrder;
    private Float quantity;
    private int inStock;

    private String completeFlag;

    public ScheduleClass(String sequence, String workOrder, Float quantity, int inStock, String completeFlag){
        this.sequence = sequence;
        this.workOrder = workOrder;
        this.quantity = quantity;
        this.inStock = inStock;
        this.completeFlag = completeFlag;
    }

    public String getSequence(){
        return sequence;
    }

    public String getWorkOrder(){
        return workOrder;
    }

    public Float getQuantity(){
        return quantity;
    }

    public int getInStock(){
        return inStock;
    }

    public String getCompleteFlag(){
        return completeFlag;
    }
}
