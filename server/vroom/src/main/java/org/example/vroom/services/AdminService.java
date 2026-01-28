package org.example.vroom.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.vroom.DTOs.DriverDTO;
import org.example.vroom.DTOs.requests.driver.DriverUpdateRequestAdminDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.DriverProfileUpdateRequest;
import org.example.vroom.enums.RequestStatus;
import org.example.vroom.mappers.DriverMapper;
import org.example.vroom.mappers.DriverProfileMapper;
import org.example.vroom.repositories.DriverProfileUpdateRequestRepository;
import org.example.vroom.repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
}
