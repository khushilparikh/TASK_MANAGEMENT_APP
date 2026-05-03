package com.example.taskmanagerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TaskManager.db";
    private static final int DATABASE_VERSION = 6;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // ================= USERS =================
        db.execSQL("CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT UNIQUE," +
                "password TEXT," +
                "role TEXT," +
                "lastSeen DATETIME)");

        // ================= TASKS =================
        db.execSQL("CREATE TABLE IF NOT EXISTS tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "deadline TEXT," +
                "assignedTo TEXT," +
                "completed INTEGER DEFAULT 0)");

        // ================= COMPLAINTS =================
        db.execSQL("CREATE TABLE IF NOT EXISTS complaints (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "employee TEXT," +
                "message TEXT," +
                "seen INTEGER DEFAULT 0," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // ================= CHAT =================
        db.execSQL("CREATE TABLE IF NOT EXISTS chat (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender TEXT," +
                "receiver TEXT," +
                "message TEXT," +
                "seen INTEGER DEFAULT 0," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // ================= DEFAULT USERS =================
        db.execSQL("INSERT OR IGNORE INTO users(email,password,role,lastSeen) VALUES " +
                "('admin@gmail.com','1234','admin',CURRENT_TIMESTAMP)");

        db.execSQL("INSERT OR IGNORE INTO users(email,password,role,lastSeen) VALUES " +
                "('employee1@gmail.com','1234','employee',CURRENT_TIMESTAMP)");

        db.execSQL("INSERT OR IGNORE INTO users(email,password,role,lastSeen) VALUES " +
                "('employee2@gmail.com','1234','employee',CURRENT_TIMESTAMP)");

        // 🔥 FIXED LINE (NO + ERROR)
        db.execSQL("INSERT OR IGNORE INTO users(email,password,role,lastSeen) VALUES " +
                "('employee3@gmail.com','1234','employee',CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS chat");
        db.execSQL("DROP TABLE IF EXISTS complaints");
        db.execSQL("DROP TABLE IF EXISTS tasks");
        db.execSQL("DROP TABLE IF EXISTS users");

        onCreate(db);
    }
}