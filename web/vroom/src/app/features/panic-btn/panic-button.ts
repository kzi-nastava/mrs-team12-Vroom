import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { NgToastService } from 'ng-angular-popup';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
import { PanicRequestDTO } from '../../core/models/panic/requests/panic-request.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';

@Component({
  selector: 'app-panic-btn',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './panic-button.html',
  styleUrl: './panic-button.css',
})
export class PanicButton {
  showPanicPopup: boolean = false
  userType: string = ''
  notifiedResponse: string = ''
  error: string = ''
  isLoading: boolean = false

  @Input() rideID: string = ''
  
  constructor(private panicService: PanicService, private toastService: NgToastService, private pns: PanicNotificationService, private cdr: ChangeDetectorRef){
    this.userType = localStorage.getItem('user_type') || '';
  }

  openPanicPopup(){
    this.showPanicPopup = true
  }

  closePanicPopup(){
    this.showPanicPopup = false
  }

  notifyPanic(){
    this.isLoading = true
    const data: PanicRequestDTO = {
        userId: Number(localStorage.getItem('user_id')),
        activatedAt: new Date()
    }

    this.panicService.panicRequest(this.rideID, data).subscribe({
      next: (response: MessageResponseDTO) => {
        this.notifiedResponse = response.message
        this.isLoading = false
        this.error = ''
        this.closePanicPopup()
        this.cdr.detectChanges()
      },
      error: (e) => {
        if(e.status === 204){
          this.error = `Content wasn't send to server` 
        }else if (e.status === 400) {
          this.error = `Couldn't process the request, please try again and hang tight in there` 
        } else if (e.status === 404) {
          this.error = 'Ride not found' 
        } else if (e.status === 500) {
          this.error = 'Internal server error. Please try again later' 
        } else {
          this.error = 'An unexpected error occurred. Please try again' 
        }
        this.notifiedResponse = ''
        this.isLoading = false
        this.cdr.detectChanges()
      }
    })

    // delete next line of code, this is only for testing purposes 
    this.pns.handlePanicNotification()
    
    //this.rideService.panicRequest()
    //this.closePanicPopup()
  }
}
