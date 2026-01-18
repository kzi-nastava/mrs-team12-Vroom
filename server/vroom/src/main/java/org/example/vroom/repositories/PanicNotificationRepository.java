package org.example.vroom.repositories;

import org.example.vroom.entities.PanicNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PanicNotificationRepository extends JpaRepository<PanicNotification, Long> {
}
