package com.example.chatapp.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String displayName;
    private String email;

    public User(String id, String username, String email) {
        this.id = id;
        this.displayName = username;
        this.email = email;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return displayName;
    }

    public void setUsername(String username) {
        this.displayName = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public String toString() {
        return displayName;
    }
}
