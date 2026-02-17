package org.example.vroom.services;


import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FcmService {
    private void sendMessage(Message message){
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    private Notification buildNotification(String title, String content){
        return Notification
                .builder()
                .setTitle(title)
                .setBody(content)
                .build();
    }

    private AndroidConfig buildHighPriorityConfig(){
        return AndroidConfig
                .builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .build();
    }

    public void sendPanicNotification(String title, String body){
        Message message = Message
                .builder()
                .setTopic("admins")
                .setNotification(buildNotification(title, body))
                .putData("type", "PANIC_ALERT")                         // custom data which app takes, for type determination
                .setAndroidConfig(buildHighPriorityConfig())            // used for close apps to set hgh priority
                .build();

        this.sendMessage(message);
    }

    public void sendStartRideNotification(String title, String body, Long userID, Long rideID) {
        Message message = Message.builder()
                .setTopic("user")
                .setNotification(buildNotification(title, body))
                .putAllData(Map.of(
                        "type", "START_RIDE",
                        "user_id", userID.toString(),
                        "ride_id", rideID.toString()
                ))
                .setAndroidConfig(buildHighPriorityConfig())
                .build();

        sendMessage(message);
    }

    public void sendFinishRideNotification(String title, String body, Long userID){
        Message message = Message.builder()
                .setTopic("user")
                .setNotification(buildNotification(title, body))
                .putAllData(Map.of(
                        "type", "FINISH_RIDE",
                        "user_id", userID.toString()
                ))
                .setAndroidConfig(buildHighPriorityConfig())
                .build();

        sendMessage(message);
    }

    public void sendAdminChatNotification(String title, String body, Long chatID){
        Message message = Message.builder()
                .setTopic("admin")
                .setNotification(buildNotification(title, body))
                .putAllData(Map.of(
                        "type", "ADMIN_CHAT",
                        "chat_id", chatID.toString()
                ))
                .setAndroidConfig(buildHighPriorityConfig())
                .build();

        sendMessage(message);
    }

    public void sendUserChatNotification(String title, String body, Long userID){
        Message message = Message.builder()
                .setTopic("user")
                .setNotification(buildNotification(title, body))
                .putAllData(Map.of(
                        "type", "USER_CHAT",
                        "user_id", userID.toString()
                ))
                .setAndroidConfig(buildHighPriorityConfig())
                .build();

        sendMessage(message);
    }
}
