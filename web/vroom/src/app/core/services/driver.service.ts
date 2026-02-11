import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { first, Observable, Subject, tap } from "rxjs";
import { HistoryMoreInfoDTO } from "../models/driver/history-more-info.dto";
import { LocationUpdate } from "../models/driver/location-update-response.dto";
import { RideHistoryResponseDTO } from "../models/driver/ride-history-response.dto";
import { MessageResponseDTO } from "../models/message-response.dto";
import { SocketProviderService } from "./socket-provider.service";

@Injectable({ providedIn: 'root' })
export class DriverService {
    private api = 'http://localhost:8080/api/drivers';
    public locationUpdates$ = new Subject<LocationUpdate>();

    constructor(
        private http: HttpClient, 
        private socketProvider: SocketProviderService
    ) {}

    initializeWebSocket(): Observable<void> {
        return this.socketProvider.onConnected.pipe(
            first(),
            tap(() => {
                this.socketProvider.stompClient.subscribe('/socket-publisher/location-updates', (message: any) => {
                    if (message.body) {
                        const coordinates = JSON.parse(message.body);
                        if (coordinates.point) {
                            this.locationUpdates$.next(coordinates);
                        }
                    }
                });
            })
        );
    }

    startTracking(): void {
        if (navigator.geolocation) {
            navigator.geolocation.watchPosition(
                (position) => {
                    this.sendCoordinates(
                        position.coords.latitude,
                        position.coords.longitude
                    );
                },
                (error) => console.error(error),
                { enableHighAccuracy: true }
            );
        }
    }

    sendCoordinates(lat: number, lng: number): void {
        this.socketProvider.send('/socket-subscriber/update-location', { lat, lng });
    }

    getRideHistoryMoreInfo(rideID : string) : Observable<HistoryMoreInfoDTO> {
        return this.http.get<HistoryMoreInfoDTO>(`${this.api}/more-info/${rideID}`)
    }


    getDriverRideHistory(
        startDate?: any,
        endDate?: any,
        sortBy?: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc'
    ): Observable<RideHistoryResponseDTO[]>{

        let params = new HttpParams().set('sort', sortBy || 'startTime,desc');
        if (startDate) {
            const start = new Date(startDate);
            params = params.set('startDate', start.toISOString());
        }
        if (endDate) {
            const end = new Date(endDate);
            params = params.set('endDate', end.toISOString());
        }

        return this.http.get<RideHistoryResponseDTO[]>(`${this.api}/rides`, { params })
    }

    createChangeStatusRequest(status: 'AVAILABLE' | 'INACTIVE'): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.api}/status`, {status})
    }
}