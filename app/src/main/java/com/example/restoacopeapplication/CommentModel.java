package com.example.restoacopeapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
public class CommentModel {
    private String id;
    private String userId;
    private String userName;
    private String comment;
    private float rating;
    private Date timestamp;
    private List<Map<String, Object>> replies;

    public CommentModel() {
        // Required empty constructor for Firebase
        replies = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public List<Map<String, Object>> getReplies() {
        return replies;
    }

    public void setReplies(List<Map<String, Object>> replies) {
        this.replies = replies;
    }
}