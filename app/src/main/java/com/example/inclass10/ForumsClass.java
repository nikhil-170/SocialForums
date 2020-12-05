package com.example.inclass10;

import com.google.firebase.Timestamp;
import java.util.HashMap;
/*
Assignment 10 InClass
InCLass10
Group1C ---Pramukh Nagendra
        ---Nikhil Surya Petiti
 */
public class ForumsClass implements Comparable<ForumsClass> {

    String forumId;
    String ownerId;
    String username;
    String title;
    String description;
    Timestamp createdAt;
    HashMap<String,Boolean> likes;
    int likeCount = 0;

    public ForumsClass(){

    }

    @Override
    public int compareTo(ForumsClass comment) {
        return comment.createdAt.compareTo(this.createdAt);
    }
}
