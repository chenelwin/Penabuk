package com.example.asus.penabuk.Model;

public class Cart {
    private Integer cart_id;
    private Book Book;
    private boolean isSelected;

    public Integer getCart_id() {
        return cart_id;
    }

    public void setCart_id(Integer cart_id) {
        this.cart_id = cart_id;
    }

    public Book getBook() {
        return Book;
    }

    public void setBook(Book book) {
        this.Book = book;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
