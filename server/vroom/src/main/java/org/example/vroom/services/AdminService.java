package org.example.vroom.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverUpdateRequestAdminDTO;
import org.example.vroom.DTOs.responses.user.UserRideHistoryResponseDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.entities.Ride;
import org.example.vroom.entities.User;
import org.example.vroom.enums.RequestStatus;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.mappers.DriverProfileMapper;
import org.example.vroom.mappers.RideMapper;
import org.example.vroom.repositories.DriverProfileUpdateRequestRepository;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.RideRepository;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.utils.SortPaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AdminService {
    @Autowired
    private DriverProfileUpdateRequestRepository requestRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private DriverProfileMapper driverProfileMapper;
    @Autowired
    private DriverMapper driverMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private SortPaginationUtils sortPaginationUtils;
    @Autowired
    private RideMapper rideMapper;

    @Transactional
    public DriverDTO approveRequest(Long requestId) throws JsonProcessingException {

        DriverProfileUpdateRequest request = requestRepository.findById(requestId)
                .orElseThrow();

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        Driver driver = request.getDriver();

        DriverDTO dto = objectMapper.readValue(
                request.getPayload(),
                DriverDTO.class
        );

        driverProfileMapper.updateEntity(driver, dto);
        driverRepository.save(driver);

        request.setStatus(RequestStatus.APPROVED);
        request.setDecidedAt(LocalDateTime.now());

        return driverProfileMapper.toDTO(driver);
    }

    @Transactional
    public void rejectRequest(Long requestId, String comment) {

        DriverProfileUpdateRequest request = requestRepository.findById(requestId)
                .orElseThrow();

        request.setStatus(RequestStatus.REJECTED);
        request.setAdminComment(comment);
        request.setDecidedAt(LocalDateTime.now());
    }
    @SneakyThrows
    public List<DriverUpdateRequestAdminDTO> getPendingDriverRequests()
            throws JsonProcessingException {

        return requestRepository.findAll()
                .stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .map(driverMapper::toAdminDTO)
                .toList();
    }

    public List<UserRideHistoryResponseDTO> getUserRideHistory(String userEmail, String sort, LocalDateTime startDate,
                                                               LocalDateTime endDate, int pageNum, int pageSize){
        Optional<User> userOptional = userRepository.findByEmail(userEmail);
        if(userOptional.isEmpty())
            throw new UserNotFoundException("This user isn't registered in system");

        Long userId = userOptional.get().getId();
        Pageable page = sortPaginationUtils.getPageable(pageNum, pageSize, sort);
        List<Ride> rides = rideRepository.userRideHistory(userId, startDate, endDate, page);

        Stream<UserRideHistoryResponseDTO> rideHistory = rides.stream().map(ride -> {
            return rideMapper.createUserRideHistoryDTO(ride);
        });

        return rideHistory.toList();
    }

    public List<UserRideHistoryResponseDTO> getUserRideHistory(String sort, LocalDateTime startDate,
                                                           LocalDateTime endDate, int pageNum, int pageSize){
        Pageable page = sortPaginationUtils.getPageable(pageNum, pageSize, sort);
        List<Ride> rides = rideRepository.userRideHistory(startDate, endDate, page);

        Stream<UserRideHistoryResponseDTO> rideHistory = rides.stream().map(ride -> {
            return rideMapper.createUserRideHistoryDTO(ride);
        });

        return rideHistory.toList();
    }
}
