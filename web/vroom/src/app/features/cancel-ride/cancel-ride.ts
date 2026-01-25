import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { GeolocationService } from '../../core/services/geolocation.service';
import { FormsModule } from '@angular/forms';
import { CancelRideRequestDTO } from '../../core/models/ride/requests/cancel-ride-req.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { StopRideRequestDTO } from '../../core/models/ride/requests/stop-ride-req.dto';
import { StoppedRideResponseDTO } from '../../core/models/ride/responses/stopped-ride-response.dto';

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

  constructor(private rideService: RideService, private geolocationService: GeolocationService, private cdr: ChangeDetectorRef){}

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
      type: this.role,
      reason: this.reason
    }

    this.rideService.cancelRideRequest(this.rideId, cancelRideData).subscribe({
      next: (response: MessageResponseDTO) => {
        this.showPopup = false
        this.reason = ''
        this.isLoading = false
        this.cdr.detectChanges()
        alert(response.message + ' redirecting....')
      },
      error: (e) => {
        this.isLoading = false
        this.cdr.detectChanges()
        alert("Failed to cancel ride. Try again.");
      }
    })
  }
}
