package org.example.vroom.entities;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends User{
    
	protected Admin(String email, String password, String firstName, String lastName, String address,
			String phoneNumber, byte[] profilePhoto, String blockedReason, int id) {
		super(email, password, firstName, lastName, address, phoneNumber, profilePhoto, blockedReason);
	}

}
