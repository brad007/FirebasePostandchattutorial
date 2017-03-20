package com.getmore.getmoreapp.firebasepostandchattutorial.models;

import java.io.Serializable;

/**
 * Created by brad on 2017/02/01.
 */

public class User implements Serializable{
    private String user;
    private String email;
    private String photUrl;
    private String Uid;

    public User() {
    }

    public User(String user) {
        this.user = user;
    }

    public User(String user, String email, String photUrl, String uid) {
        this.user = user;
        this.email = email;
        this.photUrl = photUrl;
        Uid = uid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotUrl() {
        return photUrl;
    }

    public void setPhotUrl(String photUrl) {
        this.photUrl = photUrl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
