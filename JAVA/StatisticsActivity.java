package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class StatisticsActivity extends AppCompatActivity {

    CircularProgressBar circularProgressBar;
    TextView percentageText, totalTasksText, completedTasksText, pendingTasksText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_statistics);

            circularProgressBar = findViewById(R.id.circularProgressBar);
            percentageText = findViewById(R.id.percentageText);
            totalTasksText = findViewById(R.id.totalTasks);
            completedTasksText = findViewById(R.id.completedTasks);
            pendingTasksText = findViewById(R.id.pendingTasks);

            calculateStats();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void calculateStats() {

        int total = 0;
        int completed = 0;

        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM tasks WHERE assignedTo=?",
                new String[]{TaskRepository.currentUser}
        );

        while (cursor.moveToNext()) {
            total++;
            int comp = cursor.getInt(cursor.getColumnIndexOrThrow("completed"));
            if (comp == 1) completed++;
        }

        cursor.close();

        int pending = total - completed;
        int percentage = (total == 0) ? 0 : (completed * 100) / total;

        // ✅ SAFE CHECK
        if (circularProgressBar != null) {
            circularProgressBar.setProgressWithAnimation((float) percentage, 1000L);
        }

        percentageText.setText(percentage + "%");
        totalTasksText.setText("Total Tasks: " + total);
        completedTasksText.setText("Completed Tasks: " + completed);
        pendingTasksText.setText("Pending Tasks: " + pending);
    }
}