package com.example.zappalex.popularmovies.models;


public class Review {
    private String mId;
    private String mAuthor;
    private String mContent;
    private String mUrl;

    public void setId(String id) {
        mId = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}
