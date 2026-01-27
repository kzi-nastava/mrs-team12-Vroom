import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { NgToastService } from 'ng-angular-popup';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
import { PanicRequestDTO } from '../../core/models/panic/requests/panic-request.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { ActivatedRoute } from '@angular/router';
import { Message } from 'stompjs';

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
  isLoading: boolean = false

  @Input() rideId: string = ''
  
  constructor(
    private panicService: PanicService, 
    private toastService: NgToastService, 
    private pns: PanicNotificationService, 
    private cdr: ChangeDetectorRef
  ){
    this.userType = localStorage.getItem('user_type') || ''
  }

  openPanicPopup(){
    this.showPanicPopup = true
  }

  closePanicPopup(){
    this.showPanicPopup = false
  }

  notifyPanic(){
    console.log(this.rideId, 'nesta')
    this.isLoading = true

    const data: PanicRequestDTO = {
        rideId: Number(this.rideId),
        activatedAt: new Date()
    }

    this.panicService.sendPanicRequest(data).subscribe({
        next: (res: MessageResponseDTO) => {
          this.toastService.success(
            "Administrators are notified, please hang in there", 
            'PANIC', 
            5000, 
            true, 
            true, 
            false
          )

          this.cdr.detectChanges()

          setTimeout(() => {
            this.closePanicPopup()
            this.isLoading = false
            this.cdr.detectChanges()
          }, 1000)
        },
        error: (err: MessageResponseDTO) => {
          this.toastService.danger(
            "Failed to send panic alert. Please try again", 
            'PANIC', 
            5000, 
            true, 
            true, 
            false
          )
          this.isLoading = false
          this.cdr.detectChanges()
        }
      });

    // delete next line of code, this is only for testing purposes 
    // this.pns.handlePanicNotification()

  }
}
