package org.example.vroom.services;

import org.example.vroom.DTOs.requests.panic.PanicRequestDTO;
import org.example.vroom.DTOs.responses.panic.PanicNotificationResponseDTO;
import org.example.vroom.entities.PanicNotification;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.exceptions.panic.PanicNotificationNotFound;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.PanicNotificationMapper;
import org.example.vroom.repositories.PanicNotificationRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private PanicNotificationRepository panicNotificationRepository;

    @Autowired
    private PanicNotificationMapper panicNotificationMapper;

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
        List<PanicNotification> notifications = panicNotificationRepository
                .findAll()
                .stream()
                .filter(n -> !active || !n.isResolved())
                .toList();

        if(notifications.isEmpty()) return List.of();
        else return panicNotificationMapper.createListPanicResponseDTO(notifications);
    }

    public PanicNotificationResponseDTO getPanic(Long id){
        PanicNotification notification = panicNotificationRepository.findById(id).orElse(null);

        if(notification == null) return null;
        else return panicNotificationMapper.createPanicResponseDTO(notification);
    }

    public void activatePanic(Long rideId, PanicRequestDTO data){
        Ride ride = checkRideExisting(rideId);

        Optional<User> user = userRepository.findById(data.getUserId());
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

        panicNotificationRepository.save(panic);
        rideRepository.save(ride);
    }

    public void resolvePanic(Long panicID){
        PanicNotification panic = checkPanicExisting(panicID);

        panic.setResolved(true);
        panicNotificationRepository.save(panic);
    }
}
