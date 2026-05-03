package com.example.taskmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SelectEmployeeActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_employee);

        listView = findViewById(R.id.employeeListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                TaskRepository.employees
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {

            String selectedEmployee = TaskRepository.employees[position];

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("user", selectedEmployee);
            startActivity(intent);
        });
    }
}