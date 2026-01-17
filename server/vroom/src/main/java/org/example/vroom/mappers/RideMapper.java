package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.ride.StopRideRequestDTO;
import org.example.vroom.DTOs.responses.ride.GetRideResponseDTO;
import org.example.vroom.DTOs.responses.ride.RideHistoryResponseDTO;
import org.example.vroom.DTOs.responses.ride.StoppedRideResponseDTO;
import org.example.vroom.DTOs.responses.route.GetRouteResponseDTO;
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

    public StoppedRideResponseDTO stopRide(Ride ride, StopRideRequestDTO stopRideDTO, double price) {
        GetRouteResponseDTO route = GetRouteResponseDTO
                .builder()
                .startLocationLat(ride.getRoute().getStartLocationLat())
                .startLocationLng(ride.getRoute().getStartLocationLng())
                .endLocationLat(stopRideDTO.getStopLat())
                .endLocationLng(stopRideDTO.getStopLat())
                .stops(routeMapper.mapRoutePointsDTO(ride.getRoute().getStops()))
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
                .route(routeMapper.getRouteDTO(ride.getRoute()))
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .status(ride.getStatus())
                .price(ride.getPrice())
                .panicActivated(ride.getPanicActivated())
                .build();
    }

    public GetRideResponseDTO getRideDTO(Ride ride){
        return GetRideResponseDTO
                .builder()
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
}
