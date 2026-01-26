import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CancelRideRequestDTO } from "../models/ride/requests/cancel-ride-req.dto";
import { MessageResponseDTO } from "../models/message-response.dto";
import { catchError, from, Observable, of, take, tap, throwError, timeout } from "rxjs";
import { StopRideRequestDTO } from "../models/ride/requests/stop-ride-req.dto";
import { PanicRequestDTO } from "../models/panic/requests/panic-request.dto";
import { PanicNotificationDTO } from "../models/panic/responses/panic-notification.dto";
import SockJS from "sockjs-client";
import * as Stomp from "stompjs";

@Injectable({
    providedIn: "root"
})
export class PanicService{
    private panicUrl = 'http://localhost:8080/api/panics' 
    public stompClient: any;

    constructor(private http: HttpClient) {}

    initPanicWebSockets(): Observable<void>{
        const serverUrl = 'http://localhost:8080/socket';
        const ws = new SockJS(serverUrl);
        this.stompClient = Stomp.over(ws);
        const token = localStorage.getItem('jwt');

        return new Observable<void>((observer)=>{
            this.stompClient.connect(
                { 'Authorization': `Bearer ${token}` },
                (frame: any) => {
                    console.log('Connected')
                    observer.next()
                    observer.complete()
                },
                (error: any) => {
                    console.error('Error: ', error)
                    observer.error(error)
                }
            )
        }).pipe(
            timeout(5000)
        )
    }

    sendPanicWebSockets(data: PanicRequestDTO): Observable<void>{
        const connection$: Observable<any> = (this.stompClient && this.stompClient.connected) ? of(null) : this.initPanicWebSockets()
        
        return connection$.pipe(
            tap(() => {
                if (this.stompClient?.connected) {
                    this.stompClient.send(
                        "/socket-subscriber/panic", 
                        {}, 
                        JSON.stringify(data)
                    );
                    console.log('panic sent via websocket')
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

    disconnectWebSocket(): Observable<void>{
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


    getActivePanicRequests(): Observable<PanicNotificationDTO[]>{
        const params = new HttpParams().set('active', true);
        return this.http.get<PanicNotificationDTO[]>(`${this.panicUrl}`, {params})
    }

    resolvePanicRequest(panicID: number | string): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.panicUrl}/${panicID}/resolve`, {})
    }
}