package com.example.inclass10;

import com.google.firebase.Timestamp;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class Comment implements Comparable<Comment>{
    String username;
    String body;
    String image;
    String commentId;
    String ownerId;
    Timestamp createdAt;

    public Comment() {
    }

    @Override
    public int compareTo(Comment comment) {
        return comment.createdAt.compareTo(this.createdAt);
    }
}
