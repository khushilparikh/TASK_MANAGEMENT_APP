package com.example.taskmanagerapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AssignTaskActivity extends AppCompatActivity {

    EditText titleInput, deadlineInput;
    Spinner employeeSpinner;
    Button assignBtn, logoutBtn, menuBtn;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);

        dbHelper = new DatabaseHelper(this);

        titleInput = findViewById(R.id.titleInput);
        deadlineInput = findViewById(R.id.deadlineInput);
        employeeSpinner = findViewById(R.id.employeeSpinner);
        assignBtn = findViewById(R.id.assignBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        menuBtn = findViewById(R.id.menuBtn);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        setupSpinner();
        setupCalendar();

        assignBtn.setOnClickListener(v -> assignTask());

        // HEADER
        if (navigationView.getHeaderCount() > 0) {
            View header = navigationView.getHeaderView(0);

            TextView userName = header.findViewById(R.id.userName);
            TextView userEmail = header.findViewById(R.id.userEmail);

            if (userName != null) userName.setText("Admin");
            if (userEmail != null) userEmail.setText(TaskRepository.currentUser);
        }

        // OPEN DRAWER
        menuBtn.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // NAVIGATION
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
            else if (id == R.id.nav_assigned) {
                startActivity(new Intent(this, AssignedTasksActivity.class));
            }
            else if (id == R.id.nav_complaint) {
                startActivity(new Intent(this, ComplaintActivity.class));
            }
            else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatListActivity.class));
            }
            else if (id == R.id.nav_analytics) {
                startActivity(new Intent(this, AdminAnalyticsActivity.class));
            }
            else if (id == R.id.nav_notifications) {
                startActivity(new Intent(this, NotificationActivity.class));
            }
            else if (id == R.id.nav_help) {
                startActivity(new Intent(this, HelpActivity.class));
            }
            else if (id == R.id.nav_logout) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            // 🔔 SHOW UNREAD COUNT
            int unread = getUnreadCount();

            if (unread > 0) {
                Toast.makeText(this, "🔔 " + unread + " new notifications", Toast.LENGTH_LONG).show();
            }

            drawerLayout.closeDrawers();
            return true;
        });

        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 🔥 INITIAL BADGE UPDATE
        updateNotificationBadge();
    }

    // 🔥 UPDATE BADGE WHEN RETURNING TO SCREEN
    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    private int getUnreadCount() {

        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        int count = 0;

        Cursor c1 = db.rawQuery(
                "SELECT COUNT(*) FROM chat WHERE receiver=? AND seen=0",
                new String[]{TaskRepository.currentUser}
        );

        if (c1.moveToFirst()) count += c1.getInt(0);
        c1.close();

        Cursor c2 = db.rawQuery(
                "SELECT COUNT(*) FROM complaints WHERE employee=? AND seen=0",
                new String[]{TaskRepository.currentUser}
        );

        if (c2.moveToFirst()) count += c2.getInt(0);
        c2.close();

        return count;
    }

    private void updateNotificationBadge() {

        View menuView = navigationView.getMenu()
                .findItem(R.id.nav_notifications)
                .getActionView();

        if (menuView == null) return;

        TextView badge = menuView.findViewById(R.id.badge);

        int count = getUnreadCount();

        if (count > 0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(count));
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    private void setupSpinner() {

        ArrayList<String> employeesList;

        if (TaskRepository.employees == null || TaskRepository.employees.length == 0) {
            employeesList = new ArrayList<>();
            employeesList.add("No Employees Found");
        } else {
            employeesList = new ArrayList<>(Arrays.asList(TaskRepository.employees));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                employeesList
        );

        employeeSpinner.setAdapter(adapter);
    }

    private void setupCalendar() {
        deadlineInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        month++;
                        deadlineInput.setText(String.format(Locale.getDefault(), "%d/%d/%d", day, month, year));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    private void assignTask() {

        String title = titleInput.getText().toString().trim();
        String deadline = deadlineInput.getText().toString().trim();

        Object selected = employeeSpinner.getSelectedItem();

        if (selected == null) {
            Toast.makeText(this, "Select employee", Toast.LENGTH_SHORT).show();
            return;
        }

        String employee = selected.toString();

        if (title.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (employee.equals("No Employees Found")) {
            Toast.makeText(this, "No valid employee", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO tasks(title, deadline, assignedTo, completed) VALUES(?,?,?,0)",
                new Object[]{title, deadline, employee}
        );

        Toast.makeText(this, "Task Assigned", Toast.LENGTH_SHORT).show();

        titleInput.setText("");
        deadlineInput.setText("");
    }
}