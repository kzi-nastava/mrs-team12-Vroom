package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Lob
    @Basic(fetch = FetchType.LAZY) // load when called, not on fetch
    private byte[] profilePhoto;

    @Column(nullable = true)
    private String blockedReason;


    protected User(
            String email,
            String password,
            String firstName,
            String lastName,
            String address,
            String phoneNumber,
            byte[] profilePhoto,
            String blockedReason
    ) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.profilePhoto = profilePhoto;
        this.blockedReason = blockedReason;
    }


}
