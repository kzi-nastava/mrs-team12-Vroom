package org.example.vroom.services;

import jakarta.transaction.Transactional;
import org.example.vroom.DTOs.RegisteredUserDTO;
import org.example.vroom.DTOs.requests.auth.RegisterRequestDTO;
import org.example.vroom.DTOs.responses.ride.RideResponseDTO;
import org.example.vroom.entities.RegisteredUser;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.registered_user.ActivationExpiredException;
import org.example.vroom.exceptions.user.UserAlreadyExistsException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.RegisteredUserMapper;
import org.example.vroom.mappers.RegisteredUserProfileMapper;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.RegisteredUserRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.utils.EmailService;
import org.example.vroom.utils.PasswordUtils;
import org.example.vroom.utils.SortPaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class RegisteredUserService {

    @Autowired
    private RegisteredUserRepository registeredUserRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RegisteredUserMapper registeredUserMapper;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RegisteredUserProfileMapper registeredUserProfileMapper;
    @Autowired
    private PasswordUtils passwordUtils;
    @Autowired
    private RideMapper rideMapper;
    @Autowired
    private SortPaginationUtils sortPaginationUtils;


    @Transactional
    public void createUser(RegisterRequestDTO req, MultipartFile profilePhoto) throws IOException {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            throw new UserAlreadyExistsException("User with this email already exists");

        if(!passwordUtils.isPasswordValid(req.getPassword()) || !req.getPassword().equals(req.getConfirmPassword()))
            throw new InvalidPasswordException("Password doesn't match criteria");

        RegisteredUser user = registeredUserMapper.createUser(
                req,
                profilePhoto,
                passwordEncoder.encode(req.getPassword())
        );
        user.setUserStatus(UserStatus.INACTIVE);
        user.setCreatedAt(LocalDateTime.now());

        user = registeredUserRepository.saveAndFlush(user);

        String userEmail = req.getEmail();
        String id = Long.toString(user.getId());

        try {
            emailService.sendActivationMail(user.getEmail(), String.valueOf(user.getId()));
        } catch (Exception e) {
            throw new RuntimeException("User created but email failed to send");
        }
    }

    @Transactional
    public void activateUser(Long id) {
        Optional<RegisteredUser> userOptional = registeredUserRepository.findById(id);
        if(userOptional.isEmpty())
            throw new UserNotFoundException("User not found");

        RegisteredUser user = userOptional.get();

        if(LocalDateTime.now().isAfter(user.getCreatedAt().plusDays(1)))
            throw new ActivationExpiredException("The activation link has expired (valid 24h)");

        user.setUserStatus(UserStatus.ACTIVE);
        registeredUserRepository.save(user);
    }

    public RegisteredUserDTO getMyProfile(String email) {

        RegisteredUser user = registeredUserRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
        return registeredUserProfileMapper.toDTO(user);
    }

    @Transactional
    public RegisteredUserDTO updateMyProfile(
            String email,
            RegisteredUserDTO dto
    ) {
        RegisteredUser user = registeredUserRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        registeredUserProfileMapper.updateEntity(user, dto);

        return registeredUserProfileMapper.toDTO(
                registeredUserRepository.save(user)
        );
    }

    @Transactional
    public void deleteAccount(Long userID){
        Optional<RegisteredUser> user = registeredUserRepository.findById(userID);
        if(user.isEmpty())
            return;

        registeredUserRepository.delete(user.get());
    }


    public List<RideResponseDTO> getUserRideHistory(User user, String sort, LocalDateTime startDate,
                                                    LocalDateTime endDate, int pageNum, int pageSize){

        Pageable page = sortPaginationUtils.getPageable(pageNum, pageSize, sort);
        List<Ride> rides = rideRepository.userRideHistory(user.getId(), startDate, endDate, page);

        Stream<RideResponseDTO> rideHistory = rides.stream().map(ride -> {
            return rideMapper.createUserRideHistoryDTO(ride);
        });

        return rideHistory.toList();
    }

    @Transactional
    public void deleteExpiredAccounts(LocalDateTime threshold){
        registeredUserRepository.deleteRegisteredUsersByCreatedAt(threshold);
    }
}
