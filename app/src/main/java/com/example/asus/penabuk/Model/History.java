package com.example.asus.penabuk.Model;

import android.provider.Telephony;

import java.util.List;

public class History {
    private Integer total_price;
    private String order_id;
    private String status;
    private String description;
    private Address address;
    private String createdAt;
    private List<BookHistory> details;

    public Integer getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Integer total_price) {
        this.total_price = total_price;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<BookHistory> getDetails() {
        return details;
    }

    public void setDetails(List<BookHistory> details) {
        this.details = details;
    }
}
