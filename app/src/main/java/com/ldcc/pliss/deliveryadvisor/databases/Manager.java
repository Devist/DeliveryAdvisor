package com.ldcc.pliss.deliveryadvisor.databases;

import io.realm.RealmObject;

public class Manager extends RealmObject {

    private String userName;
    private String invoice;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getInvoice() {
        return invoice;
    }
    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }
}
