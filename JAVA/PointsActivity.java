package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PointsActivity extends AppCompatActivity {

    TextView pointsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        pointsText = findViewById(R.id.pointsText);

        calculatePoints();
    }

    private void calculatePoints() {

        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM tasks WHERE assignedTo=? AND completed=1",
                new String[]{TaskRepository.currentUser}
        );

        if (cursor.moveToFirst()) {
            int completed = cursor.getInt(0);
            int points = completed * 10;

            pointsText.setText("Your Points: " + points);
        }

        cursor.close();
    }
}