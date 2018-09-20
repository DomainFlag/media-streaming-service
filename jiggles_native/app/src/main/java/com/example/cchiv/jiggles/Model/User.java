package com.example.cchiv.jiggles.model;

import java.util.ArrayList;

public class User {

    private String email;
    private String name;
    private String type;
    private String caption;
    private ArrayList<User> friends;
    private Content content;

    public User() {}

    public User(String email, String name, String type, String caption, ArrayList<User> friends, Content content) {
        this.email = email;
        this.name = name;
        this.type = type;
        this.caption = caption;
        this.friends = friends;
        this.content = content;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCaption() {
        return caption;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public Content getContent() {
        return content;
    }
}