package com.example.myapplication.model;
public class Order {
    public String id, itemName, address; public int price;
    public Order() {}
    public Order(String id, String itemName, int price, String address){
        this.id=id; this.itemName=itemName; this.price=price; this.address=address;
    }
}
