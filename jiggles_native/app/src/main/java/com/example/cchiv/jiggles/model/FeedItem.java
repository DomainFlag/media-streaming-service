package com.example.cchiv.jiggles.model;

public abstract class FeedItem {

    public class ViewType {
        public final static int VIEW_POST = 0;
        public final static int VIEW_THREAD = 1;
    }

    public abstract int getViewType();
}
