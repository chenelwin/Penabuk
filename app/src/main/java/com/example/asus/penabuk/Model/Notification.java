package com.example.asus.penabuk.Model;

public class Notification {
    private String message;
    private Integer type;
    private String object_id;
    private boolean is_readed;
    private String createdAt;
    private String typeName;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public boolean isIs_readed() {
        return is_readed;
    }

    public void setIs_readed(boolean is_readed) {
        this.is_readed = is_readed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
