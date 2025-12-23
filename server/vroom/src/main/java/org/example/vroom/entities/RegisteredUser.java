package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class RegisteredUser extends User{
    //@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;      // change with user status enum

}
