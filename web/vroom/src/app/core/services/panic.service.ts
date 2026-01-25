import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { CancelRideRequestDTO } from "../models/ride/requests/cancel-ride-req.dto";
import { MessageResponseDTO } from "../models/message-response.dto";
import { Observable } from "rxjs";
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
    private stompClient: any;

    constructor(private http: HttpClient) {
        const serverUrl = 'http://localhost:8080/socket'
        const ws = new SockJS(serverUrl)
        this.stompClient = Stomp.over(ws)

        const token = localStorage.getItem('jwt')

        const headers = {
            'Authorization': `Bearer ${token}`
        }

        this.stompClient.connect(headers, (frame: any) => {
            console.log('WebSocket connected with JWT: ' + frame)
        }, (error: any) => {
            console.error('WebSocket error: ', error);
        })
    }

    sendPanicWebSockets(data: PanicRequestDTO){
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send("/socket-subscriber/panic", {}, JSON.stringify(data))
        } else {
            console.error("STOMP didn't connect, please try again later")
        }
    }

    getActivePanicRequests(): Observable<PanicNotificationDTO[]>{
        const params = new HttpParams().set('active', true);
        return this.http.get<PanicNotificationDTO[]>(`${this.panicUrl}`, {params})
    }

    resolvePanicRequest(panicID: number | string): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.panicUrl}/${panicID}/resolve`, {})
    }
}