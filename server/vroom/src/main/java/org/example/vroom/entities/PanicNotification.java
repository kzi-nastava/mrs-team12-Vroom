package org.example.vroom.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "panic_notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PanicNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activated_by_id", nullable = false)
    private User activatedBy;

    @Column(nullable = false)
    private LocalDateTime activatedAt;

    @Column(nullable = false)
    @Builder.Default
    private boolean resolved = false;
}
