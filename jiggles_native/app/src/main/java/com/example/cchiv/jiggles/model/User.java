package com.example.cchiv.jiggles.model;

import android.os.Bundle;

import java.util.ArrayList;

public class User {

    private static final String PREF_EMAIL_KEY = "PREF_EMAIL_KEY";
    private static final String PREF_NAME_KEY = "PREF_NAME_KEY";
    private static final String PREF_PASSWORD_KEY = "PREF_PASSWORD_KEY";
    private static final String PREF_CAPTION_KEY = "PREF_CAPTION_KEY";
    private static final String PREF_SOCIAL_KEY = "PREF_SOCIAL_KEY";

    private String _id;
    private String email;
    private String name;
    private String type;
    private String caption;
    private String password;
    private ArrayList<User> friends;
    private Store store;

    public boolean social = false;

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

    public boolean resolveOwnership(FeedItem feedItem) {
        return feedItem.getLikes().containsKey(_id);
    }

    public String get_id() {
        return _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String[] getNames() {
        return name.split(" ");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public Store getStore() {
        return store;
    }

    public void resolveEncodeBundle(Bundle bundle) {
        bundle.putString(PREF_EMAIL_KEY, email);
        bundle.putString(PREF_NAME_KEY, name);
        bundle.putString(PREF_PASSWORD_KEY, password);
        bundle.putString(PREF_CAPTION_KEY, caption);
        bundle.putBoolean(PREF_SOCIAL_KEY, social);
    }

    public void resolveDecodeBundle(Bundle bundle) {
        name = bundle.getString(PREF_NAME_KEY, null);
        email = bundle.getString(PREF_EMAIL_KEY, null);
        caption = bundle.getString(PREF_CAPTION_KEY, null);
        password = bundle.getString(PREF_PASSWORD_KEY, null);
        social = bundle.getBoolean(PREF_SOCIAL_KEY, false);
    }
}