package com.mathur.android.listit;

import java.util.Date;
/**
 * Created by us48114 on 5/19/2016.
 */
public class User {
    int Id;
    String Name;
    String Password;
    String Token;
    Date lastUpdated;

    User() {
    }

    User(int userId, String name, String token, String password) {
        this.Id = userId;
        this.Name = name;
        this.Token = token;
        this.Password = password;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getPassword() {
        return this.Password;
    }

    public void setPassword(String password) {
        this.Password = password;
    }

    public String getToken() {
        return this.Token;
    }

    public void setToken(String token) {
        this.Token = token;
    }

    public int getId() {
        return this.Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }




    @Override
    public String toString() {
        return "User [id=" + Id + ", name=" + Name + ", password=" + Password
                + ", token=" + Token + "]";
    }
}
