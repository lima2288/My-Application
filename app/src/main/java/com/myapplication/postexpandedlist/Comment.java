package com.myapplication.postexpandedlist;


public class Comment {

    private String postId;
    private String id;
    private String name;
    private String email;
    private String body;

    public Comment(String postId, String id, String name, String email, String body) {
        this.postId = postId;
        this.id = id;
        this.name = name;
        this.email = email;
        this.body = body;

    }
    public String getPostId() {
        return postId;
    }
    public String getCommentId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getBody() {
        return body;
    }
}
