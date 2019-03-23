package com.example.asus.penabuk.Model;

public class Review {
    private Integer rating;
    private String review;
    private String review_author;
    private String createdAt;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReview_author() {
        return review_author;
    }

    public void setReview_author(String review_author) {
        this.review_author = review_author;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
