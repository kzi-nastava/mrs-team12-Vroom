package org.example.vroom.entities;
import jakarta.persistence.*;
import lombok.experimental.SuperBuilder;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Admin extends User{
    
}
