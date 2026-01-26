package org.example.vroom.entities;

import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;
import org.example.vroom.enums.RequestStatus;


import java.time.LocalDateTime;

@Entity
@Table(name = "driver_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverProfileUpdateRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Driver driver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;

    private LocalDateTime decidedAt;

    private String adminComment;
}
