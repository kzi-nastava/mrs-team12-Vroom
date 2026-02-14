import { GetRouteResponseDTO } from "./get-route-response.dto";

export interface UserActiveRideResponseDTO{
    rideID : number;
    driverName : string;
    vehicleInfo : string;
    route : GetRouteResponseDTO;
    passengers : string[];
    scheduledTime : Date;
    status : string;
    price : number;
    isScheduled : boolean;
}