package com.elghobaty.moviest.data.model;

import java.io.Serializable;

public class Review implements Serializable {
    private long _ID;
    private final String mBody;
    private final String mAuthor;

    public Review(long _id, String body, String author) {
        _ID = _id;
        mBody = body;
        mAuthor = author;
    }

    public Review(String body, String author) {
        mBody = body;
        mAuthor = author;
    }

    public long getID() {
        return _ID;
    }

    public String getBody() {
        return mBody;
    }

    public String getAuthor() {
        return mAuthor;
    }
}