package com.example.asus.penabuk.Model;

public class HistoryBalance {
    private Integer id;
    private Integer balance;
    private String order_id;
    private String createdAt;
    private String transactionType;
    private Integer top_up_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getTop_up_id() {
        return top_up_id;
    }

    public void setTop_up_id(Integer top_up_id) {
        this.top_up_id = top_up_id;
    }
}
