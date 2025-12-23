package org.example.vroom.entities;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import lombok.*;

@Entity
@DiscriminatorValue("REGISTERED_USER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Admin extends User{
    
}
