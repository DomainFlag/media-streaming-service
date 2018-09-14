package com.example.cchiv.jiggles.Model;

public class Image {

    private int height;
    private String url;
    private int width;

    public Image(int height, String url, int width) {
        this.height = height;
        this.url = url;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }
}