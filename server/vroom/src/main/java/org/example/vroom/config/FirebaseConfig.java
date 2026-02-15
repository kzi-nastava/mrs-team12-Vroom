package org.example.vroom.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {
    private final String PATH_TO_API_KEY = "src/main/resources/vroom-2c656-firebase-adminsdk-fbsvc-02a0d2b49e.json";
    @PostConstruct
    public void initialize(){
        try{
            FileInputStream accountKey = new FileInputStream(PATH_TO_API_KEY);

            FirebaseOptions options = FirebaseOptions
                    .builder()
                    .setCredentials(GoogleCredentials.fromStream(accountKey))
                    .build();

            if(FirebaseApp.getApps().isEmpty()){
                FirebaseApp.initializeApp(options);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
