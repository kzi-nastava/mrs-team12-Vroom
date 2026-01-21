package org.example.vroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class VroomApplication {

    public static void main(String[] args) {
        SpringApplication.run(VroomApplication.class, args);
    }

}
