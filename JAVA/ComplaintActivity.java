package com.example.taskmanagerapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class ComplaintActivity extends AppCompatActivity {

    Spinner spinner;
    EditText complaintInput;
    Button sendBtn;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        spinner = findViewById(R.id.spinnerEmployees);
        complaintInput = findViewById(R.id.complaintInput);
        sendBtn = findViewById(R.id.sendBtn);

        dbHelper = new DatabaseHelper(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                TaskRepository.employees
        );
        spinner.setAdapter(adapter);

        sendBtn.setOnClickListener(v -> sendComplaint());
    }

    private void sendComplaint() {

        String employee = spinner.getSelectedItem().toString();
        String message = complaintInput.getText().toString().trim();

        if (message.isEmpty()) {
            Toast.makeText(this, "Enter complaint", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO complaints(employee, message, seen) VALUES(?,?,0)",
                new Object[]{employee, message}
        );

        // 🔔 ADMIN CONFIRMATION NOTIFICATION
        NotificationHelper.showNotification(
                this,
                "Complaint Sent",
                "Complaint sent to " + employee
        );

        Toast.makeText(this, "Complaint Sent", Toast.LENGTH_SHORT).show();
        complaintInput.setText("");
    }
}