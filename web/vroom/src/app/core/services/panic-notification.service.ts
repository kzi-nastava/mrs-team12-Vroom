import { inject, Injectable } from "@angular/core";
import { Socket } from "ngx-socket-io";
import { Subject } from "rxjs";
import { AuthService } from "./auth.service";
import { NgToastService, ToastType } from "ng-angular-popup";

// uncomment everything when panic endpoints & websockets are implemented, this is for testing purposes only
@Injectable({
    providedIn: 'root'
})
export class PanicNotificationService{
    private panicSubject = new Subject<any>()
    panic$ = this.panicSubject.asObservable()
    
    //private socket = inject(Socket);
    //private authService = inject(AuthService);

    constructor(private toastService: NgToastService){
        console.log('init')
        //this.initPanicSocketListener()
    }

    private initPanicSocketListener(){
        /*this.socket.fromEvent('panic').subscribe((data)=>{
            const userType = this.authService.getCurrentUserType()

            if (userType && userType === 'ADMIN') {
                this.panicSubject.next(data)
                this.handlePanicNotification()
            }
        })*/
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