package com.example.asus.penabuk.Model;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {

    private Integer id;
    private String authors;
    private String original_publication_year;
    private String original_title;
    private String title;
    private Float average_rating;
    private String image_url;
    private String small_image_url;
    private Integer price;
    private Integer user_rating;
    private List<Review> reviews;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getOriginal_publication_year() {
        return original_publication_year;
    }

    public void setOriginal_publication_year(String original_publication_year) {
        this.original_publication_year = original_publication_year;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(Float average_rating) {
        this.average_rating = average_rating;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSmall_image_url() {
        return small_image_url;
    }

    public void setSmall_image_url(String small_image_url) {
        this.small_image_url = small_image_url;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getUser_rating() {
        return user_rating;
    }

    public void setUser_rating(Integer user_rating) {
        this.user_rating = user_rating;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
