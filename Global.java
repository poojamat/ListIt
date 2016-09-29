package com.mathur.android.listit;

import android.app.Application;

/**
 * Created by us48114 on 5/16/2016.
 */
public class Global extends Application {
    private int _userId;
    private String _token;

    public int get_userId() {
        return _userId;
    }

    public void set_userId(int _userId) {
        this._userId = _userId;
    }

    public String get_Token() {
        return _token;
    }

    public void set_Token(String _token) {
        this._token = _token;
    }
}

