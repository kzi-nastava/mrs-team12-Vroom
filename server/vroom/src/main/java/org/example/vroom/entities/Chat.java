package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Lazy;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Chat {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    @MapsId
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Message> messages;

    @Column()
    private LocalDateTime lastMessageTime;
}
