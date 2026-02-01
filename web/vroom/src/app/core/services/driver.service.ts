import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { catchError, Observable, of, tap, throwError, timeout } from "rxjs";
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { LocationUpdate } from "../models/driver/location-update-response.dto";
import { Subject } from "rxjs";
import { RideHistoryResponseDTO } from "../models/driver/ride-history-response.dto";
import { HttpParams } from "@angular/common/http";
import { HistoryMoreInfoDTO } from "../models/driver/history-more-info.dto";

@Injectable({
    providedIn: 'root'
})
export class DriverService{
    private api = 'http://localhost:8080/api/drivers'
    private stompClient: any;
    public locationUpdates$ = new Subject<LocationUpdate>();

    constructor(private http: HttpClient) {}

    initializeWebSocket(): Observable<void>{
        return new Observable<void>((observer) => {
            const serverUrl = 'http://localhost:8080/socket';
            const ws = new SockJS(serverUrl);
            this.stompClient = Stomp.over(ws);

            const token = localStorage.getItem('jwt');
            const headers: any = {};

            if (token){
                headers['Authorization'] =`Bearer ${token}`;
            }

            this.stompClient.connect(
                headers,
                () => {
                    this.stompClient.subscribe('/socket-publisher/location-updates', 
                        (message: any) => {
                            if (message.body) {
                                const coordinates = JSON.parse(message.body);
                                
                                if (coordinates.point) {
                                    this.locationUpdates$.next(coordinates);
                                }
                            }
                        }
                    );

                    observer.next();
                    observer.complete();
                },
                (error: any) => {
                    console.error('Error:', error);
                    observer.error(error)
                }
            )
        }).pipe(
            timeout(5000),
            catchError((err:any)=>{
                return throwError(() => err)
            })
        )
        
    }

    startTracking() {
        if (navigator.geolocation) {
            navigator.geolocation.watchPosition(
                (position) => {
                    this.sendCoordinates(
                        position.coords.latitude,
                        position.coords.longitude
                    ).subscribe();
                },(error) => {
                    console.error('Error getting location: ', error);
                },
                    {enableHighAccuracy: true,}
            );
        }
    }

    sendCoordinates(lat: number, lng: number): Observable<void> {
        const connection$: Observable<any> = (this.stompClient && this.stompClient.connected) ? of(null) : this.initializeWebSocket();

        return connection$.pipe(
            tap(()=> {
                if (this.stompClient && this.stompClient.connected) {
                    this.stompClient.send(
                        '/socket-subscriber/update-location', 
                        {}, 
                        JSON.stringify(
                            { lat, lng }
                        )
                    );
                } else {
                    throw new Error('websocket not connected after init')
                }
            }),
            catchError((err: any) => {
                console.error('Failed to send panic:', err)
                return throwError(() => err)
            })
        )
        
    }

    disconnectWebSocket(): void {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.stompClient = null
        }
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