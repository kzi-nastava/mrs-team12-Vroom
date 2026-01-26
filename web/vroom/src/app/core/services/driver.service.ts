import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { catchError, Observable, of, tap, throwError, timeout } from "rxjs";
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

    initializeWebSocket(): Observable<void>{
        return new Observable<void>((observer) => {
            const serverUrl = 'http://localhost:8080/socket';
            const ws = new SockJS(serverUrl);
            this.stompClient = Stomp.over(ws);

            this.stompClient.connect(
                {},
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

    sendCoordinates(driverId: number, lat: number, lng: number): Observable<void> {
        const connection$: Observable<any> = (this.stompClient && this.stompClient.connected) ? of(null) : this.initializeWebSocket();

        return connection$.pipe(
            tap(()=> {
                if (this.stompClient && this.stompClient.connected) {
                    this.stompClient.send(
                        '/socket-subscriber/update-location', 
                        {}, 
                        JSON.stringify({
                            driverId: driverId,
                            point: { lat: lat, lng: lng }
                        })
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

    disconnectWebSocket(): Observable<void> {
        return new Observable<void>((observer)=>{
            if (this.stompClient && this.stompClient.connected) {
                try {
                    this.stompClient.disconnect(() => {
                        this.stompClient = null
                        console.log("Socket disconnected")
                        observer.next()
                        observer.complete()
                });
                } catch (err) {
                    this.stompClient = null
                    observer.error(err)
                }
            } else {
                this.stompClient = null
                observer.next()
                observer.complete()
            }
        }).pipe(
            timeout(2000),  
            catchError((err) => {
                this.stompClient = null;
                return of(void 0);  
            })
        )
    }


    createChangeStatusRequest(status: 'AVAILABLE' | 'UNAVAILABLE'): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.api}/status`, {status})
    }
}