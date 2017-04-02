package com.getmore.getmoreapp.firebasepostandchattutorial.models;

/**
 * Created by brad on 2017/04/01.
 */

public class Notification {
    private String username;
    private String imageUrl;
    private String email;
    private String uid;
    private String text;
    private String topic;

    public Notification() {
    }

    public Notification(String username, String imageUrl, String email, String uid, String text, String topic) {
        this.username = username;
        this.imageUrl = imageUrl;
        this.email = email;
        this.uid = uid;
        this.text = text;
        this.topic = topic;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
