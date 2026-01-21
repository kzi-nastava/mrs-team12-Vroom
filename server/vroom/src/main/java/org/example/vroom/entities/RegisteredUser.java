package org.example.vroom.entities;

import org.example.vroom.enums.UserStatus;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RegisteredUser extends User{
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private UserStatus userStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    

    @Override
    public String getRoleName() {
        return "REGISTERED_USER";
    }

}
