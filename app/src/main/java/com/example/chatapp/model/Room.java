package com.example.chatapp.model;

import java.io.Serializable;

public class Room implements Serializable {

    private String id;
    private String roomname;
    private String lastMsg;


    public Room(String id, String roomname, String lastMsg) {
        this.id = id;
        this.roomname = roomname;
        this.lastMsg = lastMsg;
    }

    public Room(){}

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomname() {
        return roomname;
    }

    public void setRoomname(String roomname) {
        this.roomname = roomname;
    }
}
