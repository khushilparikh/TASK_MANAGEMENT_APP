package com.example.taskmanagerapp;

public class TaskModel {

    int id;
    String title;
    String deadline;
    String assignedTo;
    boolean completed;

    public TaskModel(int id, String title, String deadline, String assignedTo) {
        this.id = id;
        this.title = title;
        this.deadline = deadline;
        this.assignedTo = assignedTo;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDeadline() { return deadline; }
    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}