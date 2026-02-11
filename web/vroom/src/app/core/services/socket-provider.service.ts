import { Injectable } from "@angular/core";
import { first, Observable, of, ReplaySubject, timeout } from "rxjs";
import SockJS from "sockjs-client";
import * as Stomp from "stompjs";

@Injectable({ providedIn: 'root' })
export class SocketProviderService {
    private socketURL = 'http://localhost:8080/socket';
    public stompClient: any;
    private connectionSubject = new ReplaySubject<void>(1); 

    initConnection(): Observable<void> {
        if (this.stompClient?.connected) return of(undefined);
        if (this.stompClient){
            this.stompClient.disconnect();
        }

        const ws = new SockJS(this.socketURL);
        this.stompClient = Stomp.over(ws);
        this.stompClient.debug = () => {};

        const token = localStorage.getItem('jwt');
        const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

        return new Observable<void>((subscriber) => {
            this.stompClient.connect(headers, () => {
                this.connectionSubject.next(); 
                console.log("SOCKET KONEKTOVAN");
                subscriber.next();
                subscriber.complete();
            }, (err: any) => subscriber.error(err));
        });
    }

    get onConnected(): Observable<void> {
        return this.connectionSubject.asObservable();
    }

    send(destination: string, body: any): void {
        if (this.stompClient?.connected) {
            console.log("ISPIS - Connected, sending to:", destination);
            this.stompClient.send(destination, {}, JSON.stringify(body));
        } else {
            console.log("ISPIS - Not connected yet, queuing message for:", destination);
            this.onConnected.pipe(first()).subscribe({
                next: () => {
                    console.log("ISPIS - Connection acknowledged via ReplaySubject!");
                    this.stompClient.send(destination, {}, JSON.stringify(body));
                }
            });
        }
    }

    disconnect(): void {
        if (this.stompClient?.connected) {
            this.stompClient.disconnect(() => {
                this.connectionSubject = new ReplaySubject<void>(1);
            });
        }
        this.stompClient = null;
    }
}