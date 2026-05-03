package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText messageInput;
    ImageButton sendBtn;

    TextView chatUserName, lastSeenText;

    ArrayList<ChatModel> chatList = new ArrayList<>();
    ChatAdapter adapter;

    String currentUser, otherUser;
    DatabaseHelper dbHelper;

    Handler handler = new Handler();

    Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadMessages();
            markMessagesAsSeen();
            loadLastSeen();
            handler.postDelayed(this, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerViewChat);
        messageInput = findViewById(R.id.messageInput);
        sendBtn = findViewById(R.id.sendBtn);

        chatUserName = findViewById(R.id.chatUserName);
        lastSeenText = findViewById(R.id.lastSeenText);

        dbHelper = new DatabaseHelper(this);

        currentUser = TaskRepository.currentUser;
        otherUser = getIntent().getStringExtra("user");

        if (currentUser == null || otherUser == null) {
            Toast.makeText(this, "User error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatUserName.setText(otherUser);

        adapter = new ChatAdapter(chatList, currentUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadMessages();
        markMessagesAsSeen();
        loadLastSeen();

        sendBtn.setOnClickListener(v -> sendMessage());

        handler.post(refreshRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refreshRunnable);
    }

    private void sendMessage() {

        String msg = messageInput.getText().toString().trim();

        if (msg.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO chat(sender, receiver, message, seen) VALUES(?,?,?,0)",
                new Object[]{currentUser, otherUser, msg}
        );

        messageInput.setText("");
        loadMessages();
    }

    // 🔥 LAST SEEN
    private void loadLastSeen() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT lastSeen FROM users WHERE email=?",
                new String[]{otherUser}
        );

        if (cursor.moveToFirst()) {

            String lastSeen = cursor.getString(0);

            if (lastSeen != null) {
                lastSeenText.setText("Last seen: " + lastSeen);
            } else {
                lastSeenText.setText("Online");
            }
        }

        cursor.close();
    }

    // 🔥 MARK AS READ (BLUE TICKS)
    private void markMessagesAsSeen() {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "UPDATE chat SET seen=2 WHERE sender=? AND receiver=?",
                new Object[]{otherUser, currentUser}
        );
    }

    private void loadMessages() {

        chatList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT sender, message, timestamp, seen FROM chat WHERE " +
                        "(sender=? AND receiver=?) OR (sender=? AND receiver=?) " +
                        "ORDER BY id ASC",
                new String[]{currentUser, otherUser, otherUser, currentUser}
        );

        // 🔥 MARK DELIVERED
        SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
        dbWrite.execSQL(
                "UPDATE chat SET seen=1 WHERE receiver=? AND seen=0",
                new Object[]{currentUser}
        );

        while (cursor.moveToNext()) {

            String sender = cursor.getString(0);
            String message = cursor.getString(1);
            String time = cursor.getString(2);
            int seen = cursor.getInt(3);

            chatList.add(new ChatModel(sender, message, time, seen));
        }

        cursor.close();

        adapter.notifyDataSetChanged();

        if (chatList.size() > 0) {
            recyclerView.scrollToPosition(chatList.size() - 1);
        }
    }
}