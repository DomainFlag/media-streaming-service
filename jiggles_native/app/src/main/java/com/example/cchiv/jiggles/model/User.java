package com.example.cchiv.jiggles.model;

import java.util.ArrayList;

public class User {

    private String _id;
    private String email;
    private String name;
    private String type;
    private String caption;
    private ArrayList<User> friends;
    private Store store;

    public User() {}

    public User(String _id, String email, String name, String type, String caption, ArrayList<User> friends, Store store) {
        this._id = _id;
        this.email = email;
        this.name = name;
        this.type = type;
        this.caption = caption;
        this.friends = friends;
        this.store = store;
    }

    public String get_id() {
        return _id;
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

    public Store getStore() {
        return store;
    }
}