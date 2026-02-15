package org.example.vroom.services;


import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

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
}
