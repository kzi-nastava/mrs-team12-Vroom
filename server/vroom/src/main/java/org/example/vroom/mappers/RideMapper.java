package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.*;
import org.example.vroom.DTOs.responses.*;
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

    public StoppedRideDTO stopRide(Ride ride, StopRideDTO stopRideDTO, double price) {
        GetRouteDTO route = GetRouteDTO
                .builder()
                .startLocationLat(ride.getRoute().getStartLocationLat())
                .startLocationLng(ride.getRoute().getStartLocationLng())
                .endLocationLat(stopRideDTO.getStopLat())
                .endLocationLng(stopRideDTO.getStopLat())
                .stops(routeMapper.mapRoutePointsDTO(ride.getRoute().getStops()))
                .build();

        return StoppedRideDTO
                .builder()
                .driverID(ride.getDriver().getId())
                .startTime(ride.getStartTime())
                .endTime(stopRideDTO.getEndTime())
                .status(RideStatus.FINISHED)
                .price(price)
                .route(route)
                .build();
    }

    public RideHistoryDTO rideHistory(Ride ride) {
        return RideHistoryDTO
                .builder()
                .route(routeMapper.getRouteDTO(ride.getRoute()))
                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .status(ride.getStatus())
                .price(ride.getPrice())
                .panicActivated(ride.getPanicActivated())
                .build();
    }

    public GetRideDTO getRideDTO(Ride ride){
        return GetRideDTO
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
