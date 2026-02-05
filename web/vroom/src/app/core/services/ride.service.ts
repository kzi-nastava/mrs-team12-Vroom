import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CancelRideRequestDTO } from "../models/ride/requests/cancel-ride-req.dto";
import { MessageResponseDTO } from "../models/message-response.dto";
import { Observable } from "rxjs";
import { StopRideRequestDTO } from "../models/ride/requests/stop-ride-req.dto";
import { StoppedRideResponseDTO } from "../models/ride/responses/stopped-ride-response.dto";
import { LeaveReviewRequestDTO } from "../models/ride/requests/leave-review-req.dto";
import { ComplaintRequestDTO } from "../models/ride/requests/complaint-req.dto";
import { GetRouteResponseDTO } from "../models/ride/responses/get-route-response.dto";
import { map } from "rxjs/operators";
import { HttpHeaders } from "@angular/common/http";
import { GetRideResponseDTO } from '../models/ride/responses/get-ride-response.dto'

@Injectable({
    providedIn: "root"
})
export class RideService{
    private rideUrl = 'http://localhost:8080/api/rides'

    constructor(private http: HttpClient) {}

    getUserRide(): Observable<GetRideResponseDTO> {
        return this.http.get<GetRideResponseDTO>(this.rideUrl+"/user-active-ride")
    }

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

    sendComplaintRequest(rideID: string, data: ComplaintRequestDTO): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(this.rideUrl+'/complaint'+`/${rideID}`, data)
    }

    getRouteDetails(rideID: string): Observable<GetRouteResponseDTO>{
        console.log('Fetching route details for rideID:', this.rideUrl+`/${rideID}`+'/route');
        return this.http.get<GetRouteResponseDTO>(this.rideUrl+'/route'+`/${rideID}`)
    }

    getAddress(lat: number, lng: number): Observable<string> {
        const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`;
        const headers = new HttpHeaders().set('User-Agent', 'Vroom (korda.isidora@gmail.com)');
        return this.http.get<any>(url, { headers }).pipe(
        map(res => {
                if (!res || !res.display_name) return 'Unknown Address';
                const parts = res.display_name.split(',');
                return parts.slice(0, 2).map((p: string) => p.trim()).join(', ');
            })
        );
    }

    
}