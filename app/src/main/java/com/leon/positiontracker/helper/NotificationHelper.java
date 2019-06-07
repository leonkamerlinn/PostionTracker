package com.leon.positiontracker.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class NotificationHelper {
    private static NotificationHelper INSTANCE;
    private final NotificationManager mNotificationManager;

    private NotificationHelper(Context context) {
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
    public static NotificationHelper getInstance(Context context) {

        if (INSTANCE == null) {
            synchronized (NotificationHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotificationHelper(context);
                }
            }
        }

        return INSTANCE;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotificationChanel(String id, String name, int importance) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(id);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            notificationChannel.setImportance(importance);

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotificationChanel(String id, String name) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel notificationChannel = mNotificationManager.getNotificationChannel(id);
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(id, name, importance);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
            notificationChannel.setImportance(importance);

        }
    }

    @NonNull
    public NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

}
