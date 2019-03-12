package com.example.asus.penabuk.Model;

import java.io.Serializable;

public class BookPayment implements Serializable {
    private Book book;
    private Integer count;
    private Integer cart_id;

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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
