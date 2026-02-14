package com.example.vroom.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.vroom.R;
import com.example.vroom.data.local.StorageManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        Log.d("Ovde", "Ovde1");

        if (remoteMessage.getData().isEmpty() || remoteMessage.getNotification() == null) return;

        Log.d("Ovde", "Ovde2");

        Map<String, String> data = remoteMessage.getData();
        String type = data.get("type");

        Log.d("Ovde", "Ovde3");
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        determineType(type, title, body);
    }

    private void determineType(String type, String title, String body){
        if("PANIC_ALERT".equals(type) && isAdminLoggedIn()){
            Log.d("Ovde", "Ovde5");
            showPanicNotification(title, body);
        }

    }

    private void showPanicNotification(String title, String message) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL = "PANIC";

        Log.d("Ovde", "Ovde6");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "Emergency Panic Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setFullScreenIntent(null, true)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private boolean isAdminLoggedIn() {
        String type = StorageManager.getSharedPreferences(this)
                .getString("user_type", "");

        Log.d("Ovde", "Ovde4");
        return "ADMIN".equals(type);
    }
}
