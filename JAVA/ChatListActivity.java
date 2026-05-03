package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ChatListModel> list = new ArrayList<>();
    ChatListAdapter adapter;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerView = findViewById(R.id.recyclerViewChatList);
        dbHelper = new DatabaseHelper(this);

        adapter = new ChatListAdapter(list, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadChats();
    }

    private void loadChats() {

        list.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        for (String employee : TaskRepository.employees) {

            String lastMessage = "No messages yet";
            String time = "";
            int unread = 0;

            // LAST MESSAGE
            Cursor msgCursor = db.rawQuery(
                    "SELECT message, timestamp FROM chat WHERE " +
                            "(sender=? AND receiver=?) OR (sender=? AND receiver=?) " +
                            "ORDER BY id DESC LIMIT 1",
                    new String[]{TaskRepository.currentUser, employee, employee, TaskRepository.currentUser}
            );

            if (msgCursor.moveToFirst()) {
                lastMessage = msgCursor.getString(0);
                time = msgCursor.getString(1);
            }
            msgCursor.close();


            // UNREAD COUNT
            Cursor unreadCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM chat WHERE sender=? AND receiver=? AND seen=0",
                    new String[]{employee, TaskRepository.currentUser}
            );

            if (unreadCursor.moveToFirst()) {
                unread = unreadCursor.getInt(0);
            }
            unreadCursor.close();

            list.add(new ChatListModel(employee, lastMessage, time, unread));
        }

        adapter.notifyDataSetChanged();
    }
}