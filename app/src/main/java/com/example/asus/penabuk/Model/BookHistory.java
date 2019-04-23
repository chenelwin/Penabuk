package com.example.asus.penabuk.Model;

public class BookHistory {
    private String title;
    private Integer price;
    private Integer count;
    private String image_url;
    private String small_image_url;
    private String image_local;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
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

    public String getImage_local() {
        return image_local;
    }

    public void setImage_local(String image_local) {
        this.image_local = image_local;
    }
}
