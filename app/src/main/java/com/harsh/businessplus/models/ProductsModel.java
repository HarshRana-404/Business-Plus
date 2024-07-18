package com.harsh.businessplus.models;

public class ProductsModel {
    String name="";
    String hsnSac="";
    String gstPercentage="";
    Double price;

    public ProductsModel(String name, String hsnSac, String gstPercentage, Double price){
        this.name = name;
        this.hsnSac = hsnSac;
        this.gstPercentage = gstPercentage;
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public String getHsnSac() {
        return hsnSac;
    }
    public String getGstPercentage() {
        return gstPercentage;
    }

    public Double getPrice() {
        return price;
    }
}
