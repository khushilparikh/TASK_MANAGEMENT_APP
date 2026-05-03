package com.example.taskmanagerapp;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    List<TaskModel> tasks;
    DatabaseHelper dbHelper;
    Context context;

    public TaskAdapter(List<TaskModel> tasks, DatabaseHelper dbHelper, Context context) {
        this.tasks = tasks;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        TaskModel task = tasks.get(position);

        // 🔥 CARD CLICK ANIMATION
        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(100)
                    .withEndAction(() -> v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100))
                    .start();
        });

        // TEXT
        holder.title.setText(task.getTitle());
        holder.deadline.setText("Deadline: " + task.getDeadline());

        // STATUS
        if (task.isCompleted()) {
            holder.status.setText("Completed");
            holder.status.setTextColor(Color.GREEN);
            holder.progress.setProgress(100);
        } else {
            holder.status.setText("Pending");
            holder.status.setTextColor(Color.parseColor("#FFC107"));
            holder.progress.setProgress(50);
        }

        // OVERDUE CHECK
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            Date taskDate = sdf.parse(task.getDeadline());

            if (taskDate != null && taskDate.before(new Date()) && !task.isCompleted()) {
                holder.status.setText("Overdue");
                holder.status.setTextColor(Color.RED);
            }
        } catch (Exception ignored) {}

        // RESET LISTENER
        holder.completed.setOnCheckedChangeListener(null);
        holder.completed.setChecked(task.isCompleted());

        // CHECKBOX LOGIC
        holder.completed.setOnCheckedChangeListener((buttonView, isChecked) -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            TaskModel currentTask = tasks.get(pos);
            currentTask.setCompleted(isChecked);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.execSQL("UPDATE tasks SET completed=? WHERE id=?",
                    new Object[]{isChecked ? 1 : 0, currentTask.getId()});

            // 🔔 NOTIFICATION
            if (isChecked) {
                NotificationHelper.showNotification(
                        context,
                        "Task Completed",
                        "Task: " + currentTask.getTitle() + " completed"
                );
            }

            notifyItemChanged(pos);
        });

        // 🔥 CHECKBOX ANIMATION (FIXED)
        holder.completed.setOnClickListener(v -> {
            v.animate().rotation(360).setDuration(300).start();
        });

        // DELETE BUTTON
        holder.deleteBtn.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            TaskModel currentTask = tasks.get(pos);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.execSQL("DELETE FROM tasks WHERE id=?",
                    new Object[]{currentTask.getId()});

            tasks.remove(pos);
            notifyItemRemoved(pos);
        });

        // EDIT BUTTON
        holder.editBtn.setOnClickListener(v -> {

            EditText input = new EditText(context);
            input.setText(task.getTitle());

            new AlertDialog.Builder(context)
                    .setTitle("Edit Task")
                    .setView(input)
                    .setPositiveButton("Update", (dialog, which) -> {

                        String newTitle = input.getText().toString();

                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        db.execSQL("UPDATE tasks SET title=? WHERE id=?",
                                new Object[]{newTitle, task.getId()});

                        task.setTitle(newTitle);
                        notifyItemChanged(holder.getAdapterPosition());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // 🔥 FADE ANIMATION
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(300).start();
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView title, deadline, status;
        CheckBox completed;
        Button editBtn, deleteBtn;
        ProgressBar progress;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.taskTitle);
            deadline = itemView.findViewById(R.id.taskDeadline);
            status = itemView.findViewById(R.id.taskStatus);
            completed = itemView.findViewById(R.id.taskCompleted);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            progress = itemView.findViewById(R.id.taskProgress);
        }
    }
}