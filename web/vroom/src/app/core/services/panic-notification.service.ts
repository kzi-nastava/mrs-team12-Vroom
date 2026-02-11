import { Injectable } from "@angular/core";
import { first, Observable, of, Subject, tap } from "rxjs";
import { AuthService } from "./auth.service";
import { NgToastService } from "ng-angular-popup";
import { SocketProviderService } from "./socket-provider.service";

// uncomment everything when panic endpoints & websockets are implemented, this is for testing purposes only
@Injectable({
    providedIn: 'root'
})
export class PanicNotificationService{
    private panicSubject = new Subject<any>()
    panic$ = this.panicSubject.asObservable()
    
    constructor(
        private toastService: NgToastService, 
        private authService: AuthService, 
        private socketProvider : SocketProviderService
    ){}

    initalizeWebSocket(): Observable<void>{
        const userType = this.authService.getCurrentUserType
        if(userType !== 'ADMIN') return of(void 0)

        return this.socketProvider.onConnected.pipe(
            first(),
            tap(() => {
                this.socketProvider.stompClient.subscribe('/socket-publisher/panic-notifications', 
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
                })
            })
        )
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