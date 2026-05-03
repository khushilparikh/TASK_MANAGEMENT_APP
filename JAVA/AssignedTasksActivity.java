//this is for internal menu
package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AssignedTasksActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> taskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_tasks);

        listView = findViewById(R.id.listViewTasks);

        loadTasks();
    }

    private void loadTasks() {

        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT title, assignedTo, completed FROM tasks", null);

        while (cursor.moveToNext()) {
            String title = cursor.getString(0);
            String assigned = cursor.getString(1);
            int completed = cursor.getInt(2);

            String status = (completed == 1) ? "✅ Completed" : "⏳ Pending";

            taskList.add(title + "\nAssigned: " + assigned + "\nStatus: " + status);
        }

        cursor.close();

        listView.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                taskList
        ));
    }
}