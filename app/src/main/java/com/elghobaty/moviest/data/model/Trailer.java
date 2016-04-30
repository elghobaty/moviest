package com.elghobaty.moviest.data.model;

import java.io.Serializable;

public class Trailer implements Serializable{
    private long _ID;
    private final String mTitle;
    private final String mSource;

    public Trailer(long _id, String title, String source) {
        _ID = _id;
        mTitle = title;
        mSource = source;
    }

    public Trailer(String title, String source) {
        mTitle = title;
        mSource = source;
    }


    public long getID() {
        return _ID;
    }

    public String getSource() {
        return mSource;
    }

    public String getTitle() {
        return mTitle;
    }
}
