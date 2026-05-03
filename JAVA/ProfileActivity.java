package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView nameText, emailText;

    DatabaseHelper dbHelper;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);

        dbHelper = new DatabaseHelper(this);
        currentUser = TaskRepository.currentUser;

        loadProfile();
    }

    private void loadProfile() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT role FROM users WHERE email=?",
                new String[]{currentUser}
        );

        if (cursor.moveToFirst()) {

            String role = cursor.getString(0);

            // 🔥 FIX HERE
            if (role.equalsIgnoreCase("admin")) {
                nameText.setText("Admin");
            } else {
                nameText.setText("Employee");
            }

            emailText.setText(currentUser);
        }

        cursor.close();
    }
}