package com.harsh.businessplus.models;

public class BillsModel {
    String name="";
    Double total = 0.;
    String client ="";
    String date ="";
    String docID ="";
    Boolean isSelected =false;

    public BillsModel(String name, Double total, String client, String date, String docID){
        this.name = name;
        this.total = total;
        this.client = client;
        this.date = date;
        this.docID = docID;
    }

    public String getName() {
        return name;
    }
    public Double getTotal() {
        return total;
    }
    public String getClient() {
        return client;
    }
    public String getDate() {
        return date;
    }
    public String getDocId() {
        return docID;
    }
    public Boolean getSelection() {
        return isSelected;
    }
    public void setSelection(Boolean isSelected) {
        this.isSelected = isSelected;
    }


}
