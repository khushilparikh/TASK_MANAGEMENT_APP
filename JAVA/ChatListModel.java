package com.example.taskmanagerapp;

public class ChatListModel {

    String user, lastMessage, time;
    int unread;

    public ChatListModel(String user, String lastMessage, String time, int unread) {
        this.user = user;
        this.lastMessage = lastMessage;
        this.time = time;
        this.unread = unread;
    }

    public String getUser() { return user; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public int getUnread() { return unread; }
}