package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.requests.panic.PanicRequestDTO;
import org.example.vroom.DTOs.responses.panic.PanicNotificationResponseDTO;
import org.example.vroom.entities.PanicNotification;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.enums.RideStatus;
import org.example.vroom.exceptions.panic.IllegalRideException;
import org.example.vroom.exceptions.panic.PanicNotificationNotFound;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.PanicNotificationMapper;
import org.example.vroom.repositories.PanicNotificationRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PanicNotificationsService {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private PanicNotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PanicNotificationMapper panicNotificationMapper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private FcmService fcmService;

    private PanicNotification checkPanicExisting(Long panicID){
        Optional<PanicNotification> notificationOptional = notificationRepository.findById(panicID);
        if(notificationOptional.isEmpty()){
            throw new PanicNotificationNotFound("Couldn't find panic notification");
        }

        return  notificationOptional.get();
    }

    private Ride checkRideExisting(Long rideId){
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if(rideOptional.isEmpty()){
            throw new RideNotFoundException("Couldn't find ride");
        }

        return rideOptional.get();
    }

    public List<PanicNotificationResponseDTO> getPanics(boolean active){
        List<PanicNotification> notifications = notificationRepository
                .findAll()
                .stream()
                .filter(n -> !active || !n.isResolved())
                .toList();

        if(notifications.isEmpty()) return List.of();
        else return panicNotificationMapper.createListPanicResponseDTO(notifications);
    }

    public PanicNotificationResponseDTO getPanic(Long id){
        PanicNotification notification = notificationRepository.findById(id).orElseThrow(() -> new PanicNotificationNotFound("Not found"));

        return panicNotificationMapper.createPanicResponseDTO(notification);
    }

    @Transactional
    public void createPanicNotification(PanicRequestDTO data, Long id){
        Ride ride = checkRideExisting(data.getRideId());

        if(!ride.getStatus().equals(RideStatus.ONGOING))
            throw new IllegalRideException("You cannot call PANIC unless ride is active");

        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("Couldn't find user");
        }

        PanicNotification panic = PanicNotification
                .builder()
                .ride(ride)
                .activatedBy(user.get())
                .activatedAt(data.getActivatedAt())
                .build();

        ride.setPanicActivated(true);
        ride.setPanicNotification(panic);

        messagingTemplate.convertAndSend("/socket-publisher/panic-notifications", data);
        fcmService.sendPanicNotification(
                "PANIC",
                "There is PANIC on ride " + data.getRideId() + ", activated by " + user.get().getEmail()
        );

        notificationRepository.save(panic);
        rideRepository.save(ride);
    }

    public void resolvePanic(Long panicID){
        PanicNotification panic = checkPanicExisting(panicID);

        panic.setResolved(true);
        notificationRepository.save(panic);
    }
}
