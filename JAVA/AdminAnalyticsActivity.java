package com.example.taskmanagerapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;

import java.util.ArrayList;

public class AdminAnalyticsActivity extends AppCompatActivity {

    TextView totalTasks, completedTasks, pendingTasks, topEmployee;
    PieChart pieChart;
    BarChart barChart;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_admin_analytics);

            totalTasks = findViewById(R.id.totalTasks);
            completedTasks = findViewById(R.id.completedTasks);
            pendingTasks = findViewById(R.id.pendingTasks);
            topEmployee = findViewById(R.id.topEmployee);

            pieChart = findViewById(R.id.pieChart);
            barChart = findViewById(R.id.barChart);

            dbHelper = new DatabaseHelper(this);

            loadAnalytics();

        } catch (Exception e) {
            Toast.makeText(this, "Crash: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void loadAnalytics() {

        try {

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            int total = 0, completed = 0, pending = 0;

            Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM tasks", null);
            if (c1.moveToFirst()) total = c1.getInt(0);
            c1.close();

            Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE completed=1", null);
            if (c2.moveToFirst()) completed = c2.getInt(0);
            c2.close();

            Cursor c3 = db.rawQuery("SELECT COUNT(*) FROM tasks WHERE completed=0", null);
            if (c3.moveToFirst()) pending = c3.getInt(0);
            c3.close();

            totalTasks.setText("Total Tasks: " + total);
            completedTasks.setText("Completed Tasks: " + completed);
            pendingTasks.setText("Pending Tasks: " + pending);

            // TOP EMPLOYEE
            Cursor top = db.rawQuery(
                    "SELECT assignedTo, COUNT(*) FROM tasks WHERE completed=1 GROUP BY assignedTo ORDER BY COUNT(*) DESC LIMIT 1",
                    null);

            if (top.moveToFirst()) {
                topEmployee.setText("Top Employee: " + top.getString(0));
            } else {
                topEmployee.setText("Top Employee: No data");
            }
            top.close();

            setupPieChart(completed, pending);
            setupBarChart();

        } catch (Exception e) {
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupPieChart(int completed, int pending) {

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(completed, "Completed"));
        entries.add(new PieEntry(pending, "Pending"));

        PieDataSet dataSet = new PieDataSet(entries, "Tasks");

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(android.graphics.Color.GREEN);
        colors.add(android.graphics.Color.RED);

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.setCenterText("Task Status");
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    // 🔥 NEW BAR CHART
    private void setupBarChart() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT assignedTo, COUNT(*) FROM tasks WHERE completed=1 GROUP BY assignedTo",
                null);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int index = 0;

        while (cursor.moveToNext()) {

            String employee = cursor.getString(0);
            int count = cursor.getInt(1);

            entries.add(new BarEntry(index, count));
            labels.add(employee);

            index++;
        }

        cursor.close();

        BarDataSet dataSet = new BarDataSet(entries, "Tasks Completed");

        // 🔥 MODERN COLORS
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(android.graphics.Color.parseColor("#5E35B1"));
        colors.add(android.graphics.Color.parseColor("#00ACC1"));
        colors.add(android.graphics.Color.parseColor("#43A047"));
        colors.add(android.graphics.Color.parseColor("#FB8C00"));

        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.5f);

        barChart.setData(data);

        // 🔥 X-AXIS LABELS (EMPLOYEE NAMES)
        barChart.getXAxis().setValueFormatter(
                new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
        );

        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setPosition(
                com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        );

        barChart.getXAxis().setTextSize(10f);
        barChart.getXAxis().setDrawGridLines(false);

        // 🔥 Y-AXIS CLEAN
        barChart.getAxisRight().setEnabled(false);

        // 🔥 REMOVE DESCRIPTION
        barChart.getDescription().setEnabled(false);

        // 🔥 ANIMATION
        barChart.animateY(1200);

        // 🔥 EXTRA UI POLISH
        barChart.setFitBars(true);
        barChart.setDrawGridBackground(false);

        barChart.invalidate();
    }
}