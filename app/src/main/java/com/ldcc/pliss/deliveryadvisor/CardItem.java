package com.ldcc.pliss.deliveryadvisor;


public class CardItem {

    private String invoice;
    private String name;
    private String product;
    private String address;
    private String phone;
    private String message;

    public CardItem(String invoice, String name, String product, String address, String phone, String message) {
        this.invoice = invoice;
        this.name = name;
        this.product = product;
        this.address = address;
        this.phone = phone;
        this.message = message;
    }

    public String getInvoice() {
        return "송장번호 "+ invoice;
    }

    public CardItem setInvoice(String invoice) {
        this.invoice = invoice;
        return this;
    }

    public String getName() {
        return name;
    }

    public CardItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getProduct() {
        return product;
    }

    public CardItem setProduct(String product) {
        this.product = product;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public CardItem setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public CardItem setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CardItem setMessage(String message) {
        this.message = message;
        return this;
    }
}
