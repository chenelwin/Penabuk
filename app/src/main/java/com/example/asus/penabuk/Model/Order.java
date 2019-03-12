package com.example.asus.penabuk.Model;

import java.io.Serializable;

public class Order implements Serializable {
    private Integer book_id;
    private Integer count;
    private Integer cart_id;

    public Integer getBook_id() {
        return book_id;
    }

    public void setBook_id(Integer book_id) {
        this.book_id = book_id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCart_id() {
        return cart_id;
    }

    public void setCart_id(Integer cart_id) {
        this.cart_id = cart_id;
    }
}
