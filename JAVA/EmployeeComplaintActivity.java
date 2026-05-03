package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EmployeeComplaintActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> complaintList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_complaint);

        listView = findViewById(R.id.listViewComplaints);

        loadComplaints();
    }

    private void loadComplaints() {

        complaintList.clear(); // 🔥 important (avoid duplicate data)

        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT message, timestamp, seen FROM complaints WHERE employee=? ORDER BY id DESC",
                new String[]{TaskRepository.currentUser}
        );

        while (cursor.moveToNext()) {

            String message = cursor.getString(0);
            String time = cursor.getString(1);
            int seen = cursor.getInt(2);

            String status = (seen == 1) ? "✅ Seen" : "🔴 New";

            complaintList.add(
                    status + "\n" +
                            message + "\n" +
                            "Time: " + time
            );
        }

        cursor.close();

        // 🔥 VERY IMPORTANT (display data)
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                complaintList
        );

        listView.setAdapter(adapter);
    }
}