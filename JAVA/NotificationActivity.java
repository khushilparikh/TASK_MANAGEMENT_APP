package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class NotificationActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> notifications = new ArrayList<>();

    DatabaseHelper dbHelper;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.notificationList);

        dbHelper = new DatabaseHelper(this);
        currentUser = TaskRepository.currentUser;

        loadNotifications();
        markAllAsSeen(); // 🔥 MARK AS SEEN

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                notifications
        );

        listView.setAdapter(adapter);
    }

    private void loadNotifications() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        notifications.clear();

        // 🔴 COMPLAINTS (UNREAD FIRST)
        Cursor complaintCursor = db.rawQuery(
                "SELECT message, timestamp, seen FROM complaints WHERE employee=? ORDER BY seen ASC, id DESC",
                new String[]{currentUser}
        );

        while (complaintCursor.moveToNext()) {

            String msg = complaintCursor.getString(0);
            String time = complaintCursor.getString(1);
            int seen = complaintCursor.getInt(2);

            String status = (seen == 0) ? "🔴 NEW" : "✔";

            notifications.add(status + " Complaint: " + msg + "\nTime: " + time);
        }

        complaintCursor.close();

        // 💬 CHAT
        Cursor chatCursor = db.rawQuery(
                "SELECT sender, message, seen FROM chat WHERE receiver=? ORDER BY seen ASC, id DESC",
                new String[]{currentUser}
        );

        while (chatCursor.moveToNext()) {

            String sender = chatCursor.getString(0);
            String msg = chatCursor.getString(1);
            int seen = chatCursor.getInt(2);

            String status = (seen == 0) ? "🔴 NEW" : "✔";

            notifications.add(status + " Message from " + sender + ": " + msg);
        }

        chatCursor.close();

        // ✅ TASKS
        Cursor taskCursor = db.rawQuery(
                "SELECT title FROM tasks WHERE assignedTo=? AND completed=1",
                new String[]{currentUser}
        );

        while (taskCursor.moveToNext()) {

            String title = taskCursor.getString(0);

            notifications.add("✔ Task Completed: " + title);
        }

        taskCursor.close();
    }

    // 🔥 MARK ALL AS SEEN
    private void markAllAsSeen() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Chat
        db.execSQL(
                "UPDATE chat SET seen=1 WHERE receiver=?",
                new Object[]{currentUser}
        );

        // Complaints
        db.execSQL(
                "UPDATE complaints SET seen=1 WHERE employee=?",
                new Object[]{currentUser}
        );
    }
}