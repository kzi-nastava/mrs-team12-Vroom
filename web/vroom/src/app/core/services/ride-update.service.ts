import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { first, Observable, ReplaySubject, tap } from "rxjs";
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { LocationUpdate } from "../models/driver/location-update-response.dto";
import { Subject } from "rxjs";
import { RideUpdateResponseDTO } from "../models/ride/responses/ride-update-response.dto";
import { PointResponseDTO } from "../models/driver/point-response.dto";
import { SocketProviderService } from "./socket-provider.service";

@Injectable({
    providedIn: "root"
})
export class RideUpdatesService{

    private rideUpdateSubject = new Subject<RideUpdateResponseDTO>();

    constructor(private socketProvider : SocketProviderService) {}

    getRideUpdates(): Observable<RideUpdateResponseDTO> {
        return this.rideUpdateSubject.asObservable();
    }

    initRideUpdatesWebSocket(rideID: string) : Observable<void> {
      return this.socketProvider.onConnected.pipe(
        first(),
        tap(() => {
          this.socketProvider.stompClient.subscribe(`/socket-publisher/ride-duration-update/${rideID}`, (message: any) => {
            if (message.body) {
                this.rideUpdateSubject.next(JSON.parse(message.body));
            } 
          });
        })
      );
    }

    sendCoordinates(rideID: string, point: PointResponseDTO) {
      console.log('Sending coordinates:', point);
      this.socketProvider.send(`/socket-subscriber/ride-duration-update/${rideID}`, point);
    }

}