import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { Observable } from "rxjs";
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { LocationUpdate } from "../models/driver/location-update-response.dto";
import { Subject } from "rxjs";
import { RideUpdateResponseDTO } from "../models/ride/responses/ride-update-response.dto";
import { PointResponseDTO } from "../models/driver/point-response.dto";

@Injectable({
    providedIn: "root"
})
export class RideUpdatesService{
    private serverUrl = 'http://localhost:8080/socket';
    public stompClient: any;

    private rideUpdateSubject = new Subject<RideUpdateResponseDTO>();

    constructor(private http: HttpClient) {}

    getRideUpdates(): Observable<RideUpdateResponseDTO> {
        return this.rideUpdateSubject.asObservable();
    }

    initRideUpdatesWebSocket(rideID: string) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.disconnect();
        }
        const ws = new SockJS(this.serverUrl);
        this.stompClient = Stomp.over(ws);
        this.stompClient.connect({}, () => {
            this.stompClient.subscribe(`/socket-publisher/ride-duration-update/${rideID}`, (message: any) => {
                if (message.body) {
                    this.rideUpdateSubject.next(JSON.parse(message.body));
                } 
            });
        });
    }   

    sendCoordinates(rideID: string, point: PointResponseDTO) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send(
                `socket-subscriber/ride-duration-update/${rideID}`,
                {},
                JSON.stringify(point)
            );
        }
    }

    disconnectRideUpdatesWebSocket() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
    }

}