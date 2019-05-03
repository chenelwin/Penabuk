package com.example.asus.penabuk.Model;

import java.util.List;

public class ReqNotification {
    private Integer count;
    private List<Notification> notifications;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
