package com.example.taskmanagerapp;

import java.util.ArrayList;
import java.util.List;

public class TaskRepository {

    public static List<TaskModel> taskList = new ArrayList<>();

    public static String[] employees = {
            "employee1@gmail.com",
            "employee2@gmail.com",
            "employee3@gmail.com"
    };

    public static String currentUser = "";

    public static boolean isAdmin() {
        return "admin@gmail.com".equals(currentUser);
    }
}
