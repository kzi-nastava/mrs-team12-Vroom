import { inject, Injectable } from "@angular/core";
import { Socket } from "ngx-socket-io";
import { catchError, Observable, of, Subject, throwError, timeout } from "rxjs";
import { AuthService } from "./auth.service";
import { NgToastService, ToastType } from "ng-angular-popup";
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';

// uncomment everything when panic endpoints & websockets are implemented, this is for testing purposes only
@Injectable({
    providedIn: 'root'
})
export class PanicNotificationService{
    private panicSubject = new Subject<any>()
    private stompClient: any
    panic$ = this.panicSubject.asObservable()
    
    constructor(private toastService: NgToastService, private authService: AuthService){}

    initalizeWebSocket(): Observable<void>{
        const userType = this.authService.getCurrentUserType
        if(userType !== 'ADMIN') return of(void 0)

        return new Observable<void>((observer) => {
            const ws = new SockJS('http://localhost:8080/socket')
            this.stompClient = Stomp.over(ws)

            this.stompClient.connect(
                {},
                () => {
                    this.stompClient.subscribe('/socket-publisher/panic-notifications', 
                        (message: any) => {
                            if (message.body) {
                                try {
                                    const parsedData = JSON.parse(message.body)
                                    this.panicSubject.next(parsedData)
                                    this.handlePanicNotification()
                                } catch (err) {
                                    console.error('Failed to parse panic notification:', err)
                                }
                            }
                        }
                    )

                    observer.next()
                    observer.complete()
                },
                (error: any) =>{
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

    disconnectWebSocket(): void{
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.stompClient = null
        }
    }

    handlePanicNotification(){
        const audio = new Audio('/assets/sounds/warning-sound-vroom.mp3')
        audio.play().catch(err => console.error('Audio playback failed:', err));
        
        this.toastService.danger(
            'New PANIC activated, check PANIC feed for more information', 
            'PANIC', 
            5000, 
            true, 
            true, 
            false
        )
    }
}