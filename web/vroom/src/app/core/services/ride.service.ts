import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CancelRideRequestDTO } from "../models/ride/requests/cancel-ride-req.dto";
import { MessageResponseDTO } from "../models/message-response.dto";
import { Observable } from "rxjs";
import { StopRideRequestDTO } from "../models/ride/requests/stop-ride-req.dto";
import { StoppedRideResponseDTO } from "../models/ride/responses/sopped-ride-response.dto";
import { LeaveReviewRequestDTO } from "../models/ride/requests/leave-review-req.dto"

@Injectable({
    providedIn: "root"
})
export class RideService{
    private rideUrl = 'http://localhost:8080/api/rides'

    constructor(private http: HttpClient) {}

    cancelRideRequest(rideID: string, data: CancelRideRequestDTO): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(this.rideUrl+`/${rideID}`+'/cancel', data);
    }

    stopRideRequest(rideID: string, data: StopRideRequestDTO): Observable<StoppedRideResponseDTO>{
        return this.http.put<StoppedRideResponseDTO>(this.rideUrl+`/${rideID}`+'/stop', data);
    }

    leaveReviewRequest(rideID: string, data: LeaveReviewRequestDTO): Observable<MessageResponseDTO>{
        console.log(data);
        return this.http.post<MessageResponseDTO>(this.rideUrl+`/${rideID}`+'/review', data)
    }

    finishRideRequest(rideID: string): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(this.rideUrl+`/${rideID}`+'/finish', null)
    }

}