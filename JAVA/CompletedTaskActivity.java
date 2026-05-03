package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CompletedTaskActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<TaskModel> taskList = new ArrayList<>();
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TaskAdapter(taskList, new DatabaseHelper(this), this);
        recyclerView.setAdapter(adapter);

        loadCompletedTasks();
    }

    private void loadCompletedTasks() {

        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM tasks WHERE assignedTo=? AND completed=1",
                new String[]{TaskRepository.currentUser}
        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));

            TaskModel task = new TaskModel(id, title, deadline, TaskRepository.currentUser);
            task.setCompleted(true);

            taskList.add(task);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}