package com.getmore.getmoreapp.firebasepostandchattutorial.models;

import java.io.Serializable;

/**
 * Created by brad on 2017/02/01.
 */

//How firebase model doesn't account for a deeplink or a number of shares variable so let's just add
    //that quickly

public class Post implements Serializable {
    private User user;
    private String postText;
    private String postImageUrl;
    private String postId;
    private long numLikes;
    private long numComments;
    private long timeCreated;

    private String deeplink;
    private long numShares;

    public Post() {
    }

    public Post(User user, String postText, String postImageUrl, String postId, long numLikes, long numComments, long timeCreated, String deeplink, long numShares) {
        this.user = user;
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.timeCreated = timeCreated;
        this.deeplink = deeplink;
        this.numShares = numShares;
    }

    public String getDeeplink() {

        return deeplink;
    }

    public void setDeeplink(String deeplink) {
        this.deeplink = deeplink;
    }

    public long getNumShares() {
        return numShares;
    }

    public void setNumShares(long numShares) {
        this.numShares = numShares;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }
}
