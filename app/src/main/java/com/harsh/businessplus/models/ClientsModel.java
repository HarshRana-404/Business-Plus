package com.harsh.businessplus.models;

public class ClientsModel {
    String name="";
    String phone="";
    String address="";
    String state="";
    String gst="";
    Double monthlyBusiness = 0.;

    public ClientsModel(String name, String phone, String address, String state, String gst, Double monthlyBusiness){
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.state = state;
        this.gst = gst;
        this.monthlyBusiness = monthlyBusiness;
    }

    public String getName() {
        return name;
    }
    public String getPhone() {
        return phone;
    }
    public String getAddress() {
        return address;
    }
    public String getState() {
        return state;
    }
    public String getGst() {
        return gst;
    }
    public Double getMonthlyBusiness() {
        return monthlyBusiness;
    }
}
