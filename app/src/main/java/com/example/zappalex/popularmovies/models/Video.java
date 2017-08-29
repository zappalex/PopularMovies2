package com.example.zappalex.popularmovies.models;

public class Video {

    private String mId;
    private String mKey;
    private String mName;
    private String mSite;
    private String mType;

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setSite(String mSite) {
        this.mSite = mSite;
    }

    public void setType(String mType) {
        this.mType = mType;
    }
}
