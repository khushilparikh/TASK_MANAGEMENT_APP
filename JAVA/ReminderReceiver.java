package com.example.taskmanagerapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "task_channel";

        NotificationChannel channel =
                new NotificationChannel(channelId,"Task Notifications",
                        NotificationManager.IMPORTANCE_HIGH);

        manager.createNotificationChannel(channel);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context,channelId)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(),builder.build());
    }
}