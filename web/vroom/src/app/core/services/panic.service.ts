import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CancelRideRequestDTO } from "../models/ride/requests/cancel-ride-req.dto";
import { MessageResponseDTO } from "../models/message-response.dto";
import { Observable } from "rxjs";
import { StopRideRequestDTO } from "../models/ride/requests/stop-ride-req.dto";
import { PanicRequestDTO } from "../models/panic/requests/panic-request.dto";
import { PanicNotificationDTO } from "../models/panic/responses/panic-notification.dto";

@Injectable({
    providedIn: "root"
})
export class PanicService{
    private panicUrl = 'http://localhost:8080/api/panics' // when panic controller implemented use this route

    constructor(private http: HttpClient) {}

    panicRequest(rideId: string, data: PanicRequestDTO): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(`${this.panicUrl}/ride/${rideId}`, data)
    }

    getActivePanicRequests(): Observable<PanicNotificationDTO[]>{
        const params = new HttpParams().set('active', true);
        return this.http.get<PanicNotificationDTO[]>(`${this.panicUrl}`, {params})
    }

    resolvePanicRequest(panicID: number | string): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.panicUrl}/${panicID}/resolve`, {})
    }
}