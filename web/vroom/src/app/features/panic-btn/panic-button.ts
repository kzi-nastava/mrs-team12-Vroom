import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { NgToastService } from 'ng-angular-popup';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
import { PanicRequestDTO } from '../../core/models/panic/requests/panic-request.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { ActivatedRoute } from '@angular/router';

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
  
  constructor(
    private panicService: PanicService, 
    private toastService: NgToastService, 
    private pns: PanicNotificationService, 
    private cdr: ChangeDetectorRef, 
    private route: ActivatedRoute 
  ){
    this.userType = localStorage.getItem('user_type') || ''

    this.route.queryParamMap.subscribe(params => {
      this.rideID = params.get('rideId') || ''
    })
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
        rideId: Number(this.rideID),
        activatedAt: new Date()
    }

    this.panicService.sendPanicWebSockets(data).subscribe({
        next: () => {
          this.notifiedResponse = "Administrators are notified, please hang in there"
          this.error = ""

          this.cdr.detectChanges()

          setTimeout(() => {
            this.closePanicPopup()
            this.notifiedResponse = ""
            this.isLoading = false
            this.cdr.detectChanges()
          }, 1000)
        },
        error: () => {
          this.error = "Failed to send panic alert. Please try again"
          this.isLoading = false
          this.cdr.detectChanges()
        }
      });

    // delete next line of code, this is only for testing purposes 
    this.pns.handlePanicNotification()

  }
}
