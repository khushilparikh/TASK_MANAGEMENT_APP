package com.example.taskmanagerapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class TaskViewModel extends ViewModel {

    private MutableLiveData<List<TaskModel>> tasks =
            new MutableLiveData<>(TaskRepository.taskList);

    public LiveData<List<TaskModel>> getTasks() {
        return tasks;
    }

    public void refresh() {
        tasks.setValue(TaskRepository.taskList);
    }
}