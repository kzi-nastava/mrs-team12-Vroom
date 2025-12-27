package org.example.vroom.mappers;

import org.example.vroom.DTOs.requests.*;
import org.example.vroom.DTOs.responses.*;
import org.example.vroom.entities.Ride;
import org.example.vroom.enums.RideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RideMapper {
    @Autowired
    RouteMapper routeMapper;

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
}
