package com.harsh.businessplus.models;

public class BillProductModel {
    String name="";
    Double quantity = 0.;
    String gstPercentage ="";
    Double price = 0.;
    Double total = 0.;

    public BillProductModel(String name, Double quantity, String gstPercentage, Double price){
        this.name = name;
        this.quantity = quantity;
        this.gstPercentage = gstPercentage;
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public Double getQuantity() {
        return quantity;
    }
    public String  getGstPercentage() {
        return gstPercentage;
    }
    public Double getPrice() {
        return price;
    }
    public Double getTotal() {
        return getPrice()*getQuantity();
    }


}
