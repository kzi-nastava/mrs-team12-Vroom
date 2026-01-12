import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { NgToastService } from 'ng-angular-popup';
import { PanicNotificationService } from '../../core/services/panic-notification.service';

@Component({
  selector: 'app-panic',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panic.html',
  styleUrl: './panic.css',
})
export class Panic {
  showPanicPopup: boolean = false
  userType: string = '';
  
  constructor(private rideService: RideService, private toastService: NgToastService, private pns: PanicNotificationService){
    this.userType = localStorage.getItem('user_type') || '';
  }

  openPanicPopup(){
    this.showPanicPopup = true
  }

  closePanicPopup(){
    this.showPanicPopup = false
  }

  notifyPanic(){
    console.log('notify')

    // delete next line of code, this is only for testing purposes 
    this.pns.handlePanicNotification()
    
    //this.rideService.panicRequest()
    this.closePanicPopup()
  }
}
