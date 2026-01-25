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
    public stompClient: any;

    constructor(private http: HttpClient) {}

    initPanicWebSockets(): Promise<void>{
        return new Promise((resolve, reject)=>{
            const serverUrl = 'http://localhost:8080/socket';
            const ws = new SockJS(serverUrl);
            this.stompClient = Stomp.over(ws);
            const token = localStorage.getItem('jwt');

            this.stompClient.connect(
                { 'Authorization': `Bearer ${token}` },
                (frame: any) => {
                    console.log('Connected')
                    resolve()
                },
                (error: any) => {
                    console.error('Error: ', error)
                    reject(error)
                }
            );
        })
    }

    async sendPanicWebSockets(data: PanicRequestDTO){
        if(!this.stompClient)
            await this.initPanicWebSockets()

        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.send("/socket-subscriber/panic", {}, JSON.stringify(data))
        } else {
            console.error("STOMP didn't connect, please try again later")
        }
    }

     disconnectWebSocket(): Promise<void>{
        return new Promise((resolve)=>{
            if (this.stompClient && this.stompClient.connected) {
                this.stompClient.disconnect(() => {
                    this.stompClient = null;
                    console.log("Socket disconnected");
                    resolve();
                });
            } else {
                this.stompClient = null;
                resolve();
            }
        })
    }


    getActivePanicRequests(): Observable<PanicNotificationDTO[]>{
        const params = new HttpParams().set('active', true);
        return this.http.get<PanicNotificationDTO[]>(`${this.panicUrl}`, {params})
    }

    resolvePanicRequest(panicID: number | string): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.panicUrl}/${panicID}/resolve`, {})
    }
}