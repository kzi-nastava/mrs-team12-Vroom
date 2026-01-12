import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { NgToastService } from 'ng-angular-popup';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';

@Component({
  selector: 'app-panic-btn',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panic-button.html',
  styleUrl: './panic-button.css',
})
export class PanicButton {
  showPanicPopup: boolean = false
  userType: string = '';
  
  constructor(private panicService: PanicService, private toastService: NgToastService, private pns: PanicNotificationService){
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
