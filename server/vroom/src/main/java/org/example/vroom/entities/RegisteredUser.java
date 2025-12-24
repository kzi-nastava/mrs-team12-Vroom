package org.example.vroom.entities;

import org.example.vroom.enums.UserStatus;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RegisteredUser extends User{
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;     

}
