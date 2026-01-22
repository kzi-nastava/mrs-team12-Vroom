import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { Observable } from "rxjs";
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { LocationUpdate } from "../models/driver/location-update-response.dto";
import { Subject } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class DriverService{
    private api = 'http://localhost:8080/api/drivers'
    private stompClient: any;
    public locationUpdates$ = new Subject<LocationUpdate>();

    constructor(private http: HttpClient) {}

    initializeWebSocket() {
        const serverUrl = 'http://localhost:8080/socket';
        const ws = new SockJS(serverUrl);
        this.stompClient = Stomp.over(ws);

        this.stompClient.connect({}, () => {
            this.stompClient.subscribe('/socket-publisher/location-updates', (message: any) => {
                if (message.body) {
                    const coordinates = JSON.parse(message.body);
                    
                    if (coordinates.point) {
                        this.locationUpdates$.next(coordinates);
                    }
                }
            });
        });
    }

    sendCoordinates(driverId: number, lat: number, lng: number) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send(
                '/socket-subscriber/update-location', 
                {}, 
                JSON.stringify({
                    driverId: driverId,
                    point: { lat: lat, lng: lng }
                })
            );
        }
    }

    disconnectWebSocket() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
    }


    createChangeStatusRequest(id: number, status: 'AVAILABLE' | 'UNAVAILABLE'): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.api}/${id}/status`, {status})
    }
}