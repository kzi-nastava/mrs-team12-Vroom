import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { GeolocationService } from '../../core/services/geolocation.service';
import { FormsModule } from '@angular/forms';
import { CancelRideRequestDTO } from '../../core/models/ride/requests/cancel-ride-req.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { StopRideRequestDTO } from '../../core/models/ride/requests/stop-ride-req.dto';
import { StoppedRideResponseDTO } from '../../core/models/ride/responses/stopped-ride-response.dto';
import { NgToastService } from 'ng-angular-popup';
import { ActivatedRoute } from '@angular/router';
import { Message } from 'stompjs';


@Component({
  selector: 'app-cancel-ride',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cancel-ride.html',
  styleUrl: './cancel-ride.css',
})
export class CancelRide implements OnInit {
  role: String = ''
  showPopup: boolean = false
  showSuccessPopup: boolean = false
  reason: String = ''
  stoppedRideData: StoppedRideResponseDTO | null = null
  isLoading: boolean = false

  @Input() rideId: string = '';

  constructor(
    private rideService: RideService, 
    private cdr: ChangeDetectorRef, 
    private toastService: NgToastService
  ){}

  ngOnInit(): void {
      this.role = localStorage.getItem('user_type') || ''

  }


  openPopup(){
    this.showPopup = true
  }

  closePopup(){
    this.showPopup = false
    this.reason = ''
  }

  closeSuccessPopup(){
    this.showSuccessPopup = false
  }


  submitCancellation(){
    if(this.role === 'DRIVER' && this.reason === '') return

    this.isLoading = true 

    const cancelRideData: CancelRideRequestDTO = {
      reason: this.reason
    }

    this.rideService.cancelRideRequest(this.rideId, cancelRideData).subscribe({
      next: (response: MessageResponseDTO) => {
        this.showPopup = false
        this.reason = ''
        this.isLoading = false

        this.toastService.success(
            response.message + ' redirecting....', 
            'Cancellation', 
            5000, 
            true, 
            true, 
            false
        )

        this.cdr.detectChanges()
      },
      error: (e: MessageResponseDTO) => {
        this.isLoading = false
        this.toastService.danger(
            e.message,
            'Cancellation', 
            5000, 
            true, 
            true, 
            false
          )

        this.cdr.detectChanges()
      }
    })
  }
}
