package org.example.vroom.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverRegistrationRequestDTO;
import org.example.vroom.DTOs.requests.driver.DriverUpdateRequestDTO;
import org.example.vroom.DTOs.requests.driver.SetPasswordRequestDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryMoreInfoResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.entities.Ride;
import org.example.vroom.enums.DriverStatus;
import org.example.vroom.enums.RequestStatus;
import org.example.vroom.enums.UserStatus;
import org.example.vroom.exceptions.auth.InvalidPasswordException;
import org.example.vroom.exceptions.ride.RideNotFoundException;
import org.example.vroom.exceptions.user.*;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.mappers.DriverProfileMapper;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.*;
import org.example.vroom.utils.EmailService;
import org.example.vroom.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverMapper driverMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DriverProfileMapper driverProfileMapper;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RideMapper rideMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private DriverProfileUpdateRequestRepository updateRequestRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordUtils passwordUtils;
    @Autowired
    private EmailService emailService;

    public List<DriverDTO> getAvailableDrivers() {
        List<Driver> drivers =
                driverRepository.findByStatus(DriverStatus.AVAILABLE);
        return driverMapper.toDTOList(drivers);
    }

    public Optional<DriverStatus> getDriverStatus(Long id) {
        return this.driverRepository.findStatusById(id);
    }

    public DriverDTO changeStatus(Long driverId,
                                  DriverUpdateRequestDTO request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        if (driver.getStatus() == DriverStatus.UNAVAILABLE &&
                request.getStatus() == DriverStatus.INACTIVE) {
            throw new DriverStatusChangeNotAllowedException("Driver is currently on a ride");
        }

        driver.setStatus(request.getStatus());
        driverRepository.save(driver);

        return driverMapper.toDTO(driver);
    }
    @Transactional
    public DriverDTO registerDriver(DriverRegistrationRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DriverAlreadyExistsException(
                    "Driver with this email already exists"
            );
        }


        boolean hasVehicleData = hasCompleteVehicleData(request);

        if (hasVehicleData) {
            if (request.getNumberOfSeats() == null || request.getNumberOfSeats() <= 0) {
                throw new IllegalArgumentException("Number of seats must be positive");
            }
            if (request.getPetsAllowed() == null) {
                throw new IllegalArgumentException("Pets preference must be selected");
            }
            if (request.getBabiesAllowed() == null) {
                throw new IllegalArgumentException("Babies preference must be selected");
            }

            if (vehicleRepository.existsByLicenceNumber(request.getLicenceNumber())) {
                throw new IllegalArgumentException("Vehicle with this licence number already exists");
            }
        }

        Driver driver = driverMapper.toEntity(request, "aaffa4sfa6534f6asasf");
        driver.setStatus(DriverStatus.INACTIVE);
        driver.setPassword("adaisjdoiasjdasoidjaoisjdasd");

        driver = driverRepository.saveAndFlush(driver);

        try {
            System.out.println("Attempting to send email to: " + driver.getEmail());
            emailService.sendDriverActivationMail(driver.getEmail(), String.valueOf(driver.getId()));
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.err.println("EMAIL ERROR: " + e.getClass().getName());
            System.err.println("EMAIL ERROR MESSAGE: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Driver created but activation email failed to send");
        }

        return driverMapper.toDTO(driver);
    }

    private boolean hasCompleteVehicleData(DriverRegistrationRequestDTO request) {
        return request.getBrand() != null && !request.getBrand().trim().isEmpty() &&
                request.getModel() != null && !request.getModel().trim().isEmpty() &&
                request.getType() != null &&
                request.getLicenceNumber() != null && !request.getLicenceNumber().trim().isEmpty() &&
                request.getNumberOfSeats() != null &&
                request.getPetsAllowed() != null &&
                request.getBabiesAllowed() != null;
    }


    @Transactional
    public void setDriverPassword(Long driverId, SetPasswordRequestDTO request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found"));

        if (driver.getStatus() == DriverStatus.UNAVAILABLE) {
            throw new IllegalArgumentException("Account is already activated");
        }

        if (driver.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Activation link has expired");
        }

        if (!passwordUtils.isPasswordValid(request.getPassword())) {
            throw new InvalidPasswordException("Password doesn't match criteria");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new InvalidPasswordException("Passwords don't match");
        }

        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setStatus(DriverStatus.UNAVAILABLE);

        driverRepository.save(driver);
    }
    public DriverDTO getMyProfile(String email) {

        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() ->
                        new DriverNotFoundException("Driver not found"));

        return driverProfileMapper.toDTO(driver);
    }

    @Transactional
    public void requestProfileUpdate(String email, DriverDTO dto) {

        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        if (updateRequestRepository.existsByDriverAndStatus(driver, RequestStatus.PENDING)) {
            throw new PendingRequestExistsException("Already have a pending request");
        }

        DriverProfileUpdateRequest request = DriverProfileUpdateRequest.builder()
                .driver(driver)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            request.setPayload(objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize driver update request", e);
        }

        updateRequestRepository.save(request);
    }


    public Collection<RideHistoryResponseDTO> getDriverRides(Long driverId,
                                                             LocalDateTime startDate,
                                                             LocalDateTime endDate,
                                                             Sort sort)
    {
        List<Ride> rides = rideRepository.findDriverRideHistory(driverId, startDate, endDate, sort);
        Collection<RideHistoryResponseDTO> rideHistoryResponseDTOs = new ArrayList<>();
        for (Ride ride : rides) {
            System.out.println("Ride ID: " + ride.getId());
            RideHistoryResponseDTO dto = rideMapper.rideHistory(ride);
            rideHistoryResponseDTOs.add(dto);
            System.out.println("Ride: " + ride);
            System.out.println(dto);
        }
        return rideHistoryResponseDTOs;
    }

    public RideHistoryMoreInfoResponseDTO getRideMoreInfo(Long rideID){
        Optional<Ride> rideOpt = this.rideRepository.findById(rideID);
        if (rideOpt.isPresent()) {
            Ride ride = rideOpt.get();
            return this.rideMapper.getRideHistoryMoreInfo(ride);
        }
        throw new RideNotFoundException("Ride not found");
    }

    public void changeStatus(Long driverID, DriverStatus status){
        Optional<Driver> driver = driverRepository.findById(driverID);
        if (driver.isEmpty() || driver.get().getStatus() == DriverStatus.BLOCKED)
            throw new DriverNotFoundException("Driver not found");

        if(driver.get().getStatus().equals(status)){
            throw new DriverStatusChangeNotAllowedException("Driver is blocked");
        }

        driver.get().setStatus(status);
        driverRepository.save(driver.get());
    }
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword, String confirmNewPassword) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        if (!passwordEncoder.matches(oldPassword, driver.getPassword())) {
            throw new InvalidPasswordException("Old password is incorrect");
        }

        if (!passwordUtils.isPasswordValid(newPassword)) {
            throw new InvalidPasswordException("New password doesn't match criteria");
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new InvalidPasswordException("New passwords do not match");
        }

        if (passwordEncoder.matches(newPassword, driver.getPassword())) {
            throw new InvalidPasswordException("New password must be different from the old password");
        }

        driver.setPassword(passwordEncoder.encode(newPassword));
        driverRepository.save(driver);
    }
}
