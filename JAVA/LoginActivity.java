package com.example.taskmanagerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);

        dbHelper = new DatabaseHelper(this);

        loginBtn.setOnClickListener(v -> loginUser());
        SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();

        db.execSQL(
                "UPDATE users SET lastSeen=CURRENT_TIMESTAMP WHERE email=?",
                new Object[]{TaskRepository.currentUser}
        );
    }

    private void loginUser() {

        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM users WHERE email=? AND password=?",
                new String[]{userEmail, userPassword}
        );

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        if (cursor.moveToFirst()) {

            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));

            // 🔥 SET CURRENT USER (IMPORTANT FOR CHAT)
            TaskRepository.currentUser = userEmail;

            // 🔥 UPDATE LAST SEEN (AFTER SUCCESS LOGIN)
            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();
            dbWrite.execSQL(
                    "UPDATE users SET lastSeen=CURRENT_TIMESTAMP WHERE email=?",
                    new Object[]{userEmail}
            );

            Intent intent;

            if (role.equalsIgnoreCase("admin")) {
                intent = new Intent(this, AssignTaskActivity.class);
            } else {
                intent = new Intent(this, EmployeeDashboardActivity.class);
            }

            // 🔥 PASS USER (VERY IMPORTANT)
            intent.putExtra("user", userEmail);

            startActivity(intent);
            finish();
        }

        cursor.close();
    }
}