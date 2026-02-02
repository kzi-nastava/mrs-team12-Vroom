package org.example.vroom.controllers;

import lombok.RequiredArgsConstructor;

import org.example.vroom.DTOs.responses.ride.RideReportDTO;
import org.example.vroom.entities.Driver;
import org.example.vroom.entities.User;
import org.example.vroom.repositories.DriverRepository;
import org.example.vroom.repositories.UserRepository;
import org.example.vroom.services.RideStatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class RideStatisticsController {

    private final RideStatisticsService statisticsService;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @GetMapping("/me")
    public ResponseEntity<RideReportDTO> myReport(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        User u = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));


        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;


        return ResponseEntity.ok(
                statisticsService.passengerReport(u.getId(), fromDateTime, toDateTime)
        );
    }


    @GetMapping("/driver/me")
    public ResponseEntity<RideReportDTO> myDriverReport(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        Driver d = driverRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new RuntimeException("Driver not found"));


        LocalDateTime fromDT = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDT = to != null ? to.atTime(23, 59, 59) : null;

        return ResponseEntity.ok(
                statisticsService.driverReport(
                        d.getId(),
                        fromDT,
                        toDT
                )
        );
    }


    @GetMapping("/admin")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RideReportDTO> adminReport(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        return ResponseEntity.ok(
                statisticsService.adminReport(from, to)
        );
    }

    @GetMapping("/admin/user/{userId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RideReportDTO> adminUserReport(
            @PathVariable Long userId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        return ResponseEntity.ok(
                statisticsService.passengerReport(userId, from, to)
        );
    }

    @GetMapping("/admin/driver/{driverId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RideReportDTO> adminDriverReport(
            @PathVariable Long driverId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to
    ) {
        return ResponseEntity.ok(
                statisticsService.driverReport(driverId, from, to)
        );
    }
}
