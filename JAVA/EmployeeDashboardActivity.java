package com.example.taskmanagerapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDashboardActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button logoutBtn, statsBtn;
    ImageButton menuBtn;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    TaskAdapter adapter;
    List<TaskModel> taskList = new ArrayList<>();
    DatabaseHelper dbHelper;

    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);

        currentUser = getIntent().getStringExtra("user");

        if (currentUser == null) {
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        logoutBtn = findViewById(R.id.logoutBtn);
        statsBtn = findViewById(R.id.statsBtn);
        menuBtn = findViewById(R.id.menuBtn);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // ✅ FIXED BUTTON (FINAL)
        menuBtn.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, dbHelper, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadTasks();
        checkComplaints();

        if (navigationView != null && navigationView.getHeaderCount() > 0) {

            View header = navigationView.getHeaderView(0);

            TextView userName = header.findViewById(R.id.userName);
            TextView userEmail = header.findViewById(R.id.userEmail);

            if (userName != null) userName.setText("Welcome");
            if (userEmail != null) userEmail.setText(currentUser);
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {

                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                }
                else if (id == R.id.nav_completed) {
                    startActivity(new Intent(this, CompletedTaskActivity.class));
                }
                else if (id == R.id.nav_points) {
                    startActivity(new Intent(this, PointsActivity.class));
                }
                else if (id == R.id.nav_stats) {
                    startActivity(new Intent(this, StatisticsActivity.class));
                }
                else if (id == R.id.nav_help) {
                    startActivity(new Intent(this, HelpActivity.class));
                }
                else if (id == R.id.nav_complaints) {
                    startActivity(new Intent(this, EmployeeComplaintActivity.class));
                }
                else if (id == R.id.nav_chat) {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("user", "admin@gmail.com");
                    startActivity(intent);
                }
                else if (id == R.id.nav_logout) {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                if (drawerLayout != null) {
                    drawerLayout.closeDrawers();
                }

                return true;
            });
        }

        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        statsBtn.setOnClickListener(v ->
                startActivity(new Intent(this, StatisticsActivity.class)));
    }

    private void checkComplaints() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id, message FROM complaints WHERE employee=? AND seen=0",
                new String[]{currentUser}
        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String message = cursor.getString(1);

            NotificationHelper.showNotification(
                    this,
                    "New Complaint",
                    message
            );

            SQLiteDatabase dbWrite = dbHelper.getWritableDatabase();

            dbWrite.execSQL(
                    "UPDATE complaints SET seen=1 WHERE id=?",
                    new Object[]{id}
            );
        }

        cursor.close();
    }

    private void loadTasks() {

        taskList.clear();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM tasks WHERE assignedTo=?",
                new String[]{currentUser}
        );

        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
            int completed = cursor.getInt(cursor.getColumnIndexOrThrow("completed"));

            TaskModel task = new TaskModel(id, title, deadline, currentUser);
            task.setCompleted(completed == 1);

            taskList.add(task);
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}