package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisteredUser extends User{
    //@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String status;      // change with user status enum


    @Builder
    public RegisteredUser(
            String email,
            String password,
            String firstName,
            String lastName,
            String address,
            String phoneNumber,
            String status
    )
    {
        super(email, password, firstName, lastName, address, phoneNumber, null, null);
        this.status = status;
    }
}
