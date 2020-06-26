package com.example.chatapp.model;

import java.io.Serializable;

public class Chat implements Serializable {

    private String sender;
    private String room;
    private String message;
    private String senderName;

    public Chat(String sender, String receiver, String message, String senderName) {
        this.sender = sender;
        this.room = receiver;
        this.message = message;
        this.senderName = senderName;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}