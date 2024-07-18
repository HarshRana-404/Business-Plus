package com.harsh.businessplus.models;

public class TotalsModel {
    String docID="";
    Double subTotal = 0.;
    Double gstTotal = 0.;
    Double grandTotal = 0.;

    public TotalsModel(String docID, Double subTotal, Double gstTotal, Double grandTotal){
        this.docID = docID;
    }

    public String getDocID() {
        return docID;
    }
    public Double getSubTotal() {
        return subTotal;
    }
    public Double getGstTotal() {
        return gstTotal;
    }
    public Double getGrandTotal() {
        return grandTotal;
    }



}
