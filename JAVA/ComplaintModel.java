package com.example.taskmanagerapp;

public class ComplaintModel {

    String employee;
    String message;

    public ComplaintModel(String employee, String message) {
        this.employee = employee;
        this.message = message;
    }

    public String getEmployee() {
        return employee;
    }

    public String getMessage() {
        return message;
    }
}