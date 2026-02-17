package com.example.vroom.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.vroom.R;
import com.example.vroom.activities.AdminActivity;
import com.example.vroom.activities.MainActivity;
import com.example.vroom.data.local.StorageManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().isEmpty() || remoteMessage.getNotification() == null) return;

        Map<String, String> data = remoteMessage.getData();
        String type = data.get("type");
        String user_id = data.get("user_id");
        String ride_id = data.get("ride_id");
        String chat_id = data.get("chat_id");

        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        determineType(type, title, body, user_id, ride_id, chat_id);
    }

    private void determineType(String type, String title, String body, String userID, String rideID, String chatID){
        String id = userID();
        String role = userType();
        if("PANIC_ALERT".equals(type) && isAdminLoggedIn()){
            showPanicNotification(title, body);
        }
        if ("START_RIDE".equals(type) && "REGISTERED_USER".equals(role) && userID.equals(id)){
            showStartRideNotification(title, body, rideID);
        }
        if ("FINISH_RIDE".equals(type) && "REGISTERED_USER".equals(role) && userID.equals(id)){
            showFinishRideNotification(title, body);
        }
        if ("ADMIN_CHAT".equals(type) && "ADMIN".equals(role)){
            showAdminChatNotification(title, body, chatID);
        }
        if ("USER_CHAT".equals(type) && ("REGISTERED_USER".equals(role) || "DRIVER".equals(role)) && userID.equals(id)){
            showUserChatNotification(title, body);
        }

    }

    private void showUserChatNotification(String title, String message){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL = "USER_CHAT";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "User Chat Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_email)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(createUserChatIntent())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showAdminChatNotification(String title, String message, String chatID){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL = "ADMIN_CHAT";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "Admin Chat Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_email)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(createAdminChatIntent(chatID))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showFinishRideNotification(String title, String message){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL = "FINISH_RIDE";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "Finish Ride Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void showStartRideNotification(String title, String message, String rideID){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String CHANNEL = "START_RIDE";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL,
                    "Start Ride Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentTitle("Vroom - " + title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(createPendingIntentRideTracking(rideID))
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        manager.notify((int) System.currentTimeMillis(), builder.build());
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

    private PendingIntent createPendingIntentRideTracking(String rideID) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        intent.putExtra("RIDE_ID", Long.valueOf(rideID));
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private PendingIntent createUserChatIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("USER_CHAT", true);

        return PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private PendingIntent createAdminChatIntent(String chatID) {
        Intent intent = new Intent(this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("CHAT_ID", Long.valueOf(chatID));

        return PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private String userType(){
        return StorageManager.getSharedPreferences(this).getString("user_type", "");
    }

    private String userID(){
        return StorageManager.getSharedPreferences(this).getString("user_id", "");
    }


    private boolean isAdminLoggedIn() {
        String type = StorageManager.getSharedPreferences(this)
                .getString("user_type", "");

        Log.d("Ovde", "Ovde4");
        return "ADMIN".equals(type);
    }
}
