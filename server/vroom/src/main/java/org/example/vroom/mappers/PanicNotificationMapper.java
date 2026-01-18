package org.example.vroom.mappers;

import org.example.vroom.DTOs.responses.panic.PanicNotificationResponseDTO;
import org.example.vroom.entities.PanicNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PanicNotificationMapper {

    public PanicNotificationResponseDTO createPanicResponseDTO(PanicNotification notification){
        return PanicNotificationResponseDTO
                .builder()
                .id(notification.getId())
                .rideID(notification.getRide().getId())
                .activatedById(notification.getActivatedBy().getId())
                .activatedAt(notification.getActivatedAt())
                .build();
    }

    public List<PanicNotificationResponseDTO> createListPanicResponseDTO(List<PanicNotification> notifications){
        return notifications.stream()
                .map(notification -> PanicNotificationResponseDTO.builder()
                        .id(notification.getId())
                        .rideID(notification.getRide().getId())
                        .activatedById(notification.getActivatedBy().getId())
                        .activatedAt(notification.getActivatedAt())
                        .build())
                .toList();
    }
}
