package com.ldcc.pliss.deliveryadvisor.databases;

import io.realm.RealmObject;

/**
 * Created by pliss on 2018. 2. 23..
 */

public class Manager extends RealmObject {

    private String userName;
    private String currentInvoice;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCurrentInvoice() {
        return currentInvoice;
    }

    public void setCurrentInvoice(String currentInvoice) {
        this.currentInvoice = currentInvoice;
    }
}
