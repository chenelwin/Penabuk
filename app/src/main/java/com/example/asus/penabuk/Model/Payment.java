package com.example.asus.penabuk.Model;

import java.io.Serializable;
import java.util.List;

public class Payment implements Serializable {
    private String password;
    private Integer address_id;
    private List<Order> orders;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAddress_id() {
        return address_id;
    }

    public void setAddress_id(Integer address_id) {
        this.address_id = address_id;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
