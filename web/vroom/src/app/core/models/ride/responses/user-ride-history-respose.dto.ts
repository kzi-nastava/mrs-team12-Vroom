import { RideStatus } from "../../../../features/driver-active-ride/ride.model";
import { GetRouteResponseDTO } from "./get-route-response.dto";

export interface UserRideHistoryResponseDTO{
    rideId: number;

    driverFirstName: string;
    driverLastName: string;

    startTime: Date;
    endTime: Date;
    passengers: string[];
    price: number;
    status: RideStatus;
    complaints: string[];
    panicActivated: boolean;
    driverRating: number;
    vehicleRating: number;
    comment: string;
    cancelReason: string;

    route: GetRouteResponseDTO;
}