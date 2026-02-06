package org.example.vroom.mappers;

import org.example.vroom.DTOs.RideDTO;
import org.example.vroom.DTOs.requests.ride.StopRideRequestDTO;
import org.example.vroom.DTOs.responses.ride.*;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideResponseDTO;
import org.example.vroom.entities.Ride;
import org.example.vroom.enums.RideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RideMapper {
    @Autowired
    RouteMapper routeMapper;
    @Autowired
    DriverMapper driverMapper;

    public StoppedRideResponseDTO stopRide(Ride ride, StopRideRequestDTO stopRideDTO, double price, String endAddress) {
        GetRouteResponseDTO route = GetRouteResponseDTO
                .builder()
                .startLocationLat(ride.getRoute().getStartLocationLat())
                .startLocationLng(ride.getRoute().getStartLocationLng())
                .endLocationLat(stopRideDTO.getStopLat())
                .endLocationLng(stopRideDTO.getStopLng())
                .stops(routeMapper.mapRoutePointsDTO(ride.getRoute().getStops()))
                .startAddress(ride.getRoute().getStartAddress())
                .endAddress(endAddress)
                .build();

        return StoppedRideResponseDTO
                .builder()
                .driverID(ride.getDriver().getId())
                .startTime(ride.getStartTime())
                .endTime(stopRideDTO.getEndTime())
                .status(RideStatus.FINISHED)
                .price(price)
                .route(route)
                .build();
    }

    public RideHistoryResponseDTO rideHistory(Ride ride) {
        return RideHistoryResponseDTO
                .builder()
                .rideId(ride.getId())
                .startAddress(ride.getRoute().getStartAddress())
                .endAddress(ride.getRoute().getEndAddress())
                .startTime(ride.getStartTime())
                .status(ride.getStatus())
                .price(ride.getPrice())
                .panicActivated(ride.getPanicActivated())
                .build();
    }

    public GetActiveRideInfoDTO getActiveRideInfo(Ride ride) {
        return GetActiveRideInfoDTO
                .builder()
                .startAddress(ride.getRoute().getStartAddress())
                .endAddress(ride.getRoute().getEndAddress())
                .startTime(ride.getStartTime())
                .creatorName(ride.getPassenger().getFirstName() + " " + ride.getPassenger().getLastName())
                .driverName(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName())
                .passengerEmails(ride.getPassengers())
                .build();
    }

    public RideHistoryMoreInfoResponseDTO getRideHistoryMoreInfo(Ride ride) {
        return RideHistoryMoreInfoResponseDTO
                .builder()
                .rideID(ride.getId())
                .passengers(ride.getPassengers())
                .status(ride.getStatus())
                .cancelReason(ride.getCancelReason())
                .complaints(ride.getComplaints())
                .driverRating(ride.getDriverRating())
                .vehicleRating(ride.getVehicleRating())
                .comment(ride.getComment())
                .build();
    }

    public GetRideResponseDTO getRideDTO(Ride ride){
        return GetRideResponseDTO
                .builder()
                .rideID(ride.getId())
                .route(routeMapper.getRouteDTO(ride.getRoute()))
                .driver(driverMapper.toDriverRideDTO(ride.getDriver()))
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .status(ride.getStatus())
                .price(ride.getPrice())
                .panicActivated(ride.getPanicActivated())
                .passengers(ride.getPassengers())
                .complaints(ride.getComplaints())
                .driverRating(ride.getDriverRating())
                .vehicleRating(ride.getVehicleRating())
                .build();
    }

    public AcceptedRideDTO acceptedRide(Ride ride) {
        return AcceptedRideDTO.builder()
                .startAddress(ride.getRoute().getStartAddress())
                .endAddress(ride.getRoute().getEndAddress())
                .driverName(ride.getDriver().getFirstName() + " " + ride.getDriver().getLastName())
                .vehicleInfo(ride.getDriver().getVehicle().getBrand() + " " + ride.getDriver().getVehicle().getModel())
                .licensePlate(ride.getDriver().getVehicle().getLicenceNumber())
                .build();
    }

    public RideDTO toRideDTO(Ride ride) {
        if (ride == null) return null;

        return RideDTO.builder()
                .id(ride.getId())
                .status(ride.getStatus())
                .driver(driverMapper.toDTO(ride.getDriver()))
                .route(routeMapper.getRouteDTO(ride.getRoute()))
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .passengers(ride.getPassengers())
                .price(ride.getPrice())
                .build();
    }

    public RideResponseDTO createUserRideHistoryDTO(Ride ride){
        if(ride == null) return null;

        return RideResponseDTO.builder()
                .rideId(ride.getId())
                .driverFirstName(ride.getDriver().getFirstName())
                .driverLastName(ride.getDriver().getLastName())
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .passengers(ride.getPassengers())
                .price(ride.getPrice())
                .status(ride.getStatus())
                .complaints(ride.getComplaints())
                .panicActivated(ride.getPanicActivated())
                .driverRating(ride.getDriverRating())
                .vehicleRating(ride.getVehicleRating())
                .comment(ride.getComment())
                .cancelReason(ride.getCancelReason())
                .route(routeMapper.getRouteDTO(ride.getRoute()))
                .build();
    }

}
