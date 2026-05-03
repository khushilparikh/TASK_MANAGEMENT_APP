package com.example.taskmanagerapp;

public class ChatModel {

    String sender, message, time;
    int seen; // 🔥 IMPORTANT

    // ✅ CONSTRUCTOR
    public ChatModel(String sender, String message, String time, int seen) {
        this.sender = sender;
        this.message = message;
        this.time = time;
        this.seen = seen;
    }

    // ✅ GETTERS
    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

    public int getSeen() {
        return seen;
    }

    // ✅ OPTIONAL SETTER (future use)
    public void setSeen(int seen) {
        this.seen = seen;
    }
}