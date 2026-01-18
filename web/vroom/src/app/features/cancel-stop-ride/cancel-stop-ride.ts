import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { GeolocationService } from '../../core/services/geolocation.service';
import { FormsModule } from '@angular/forms';
import { CancelRideRequestDTO } from '../../core/models/ride/requests/cancel-ride-req.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { StopRideRequestDTO } from '../../core/models/ride/requests/stop-ride-req.dto';
import { StoppedRideResponseDTO } from '../../core/models/ride/responses/sopped-ride-response.dto';

@Component({
  selector: 'app-cancel-ride',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cancel-stop-ride.html',
  styleUrl: './cancel-stop-ride.css',
})
export class CancelStopRide implements OnInit {
  role: String = ''
  showPopup: boolean = false
  showSuccessPopup: boolean = false
  reason: String = ''
  stoppedRideData: StoppedRideResponseDTO | null = null
  isLoading: boolean = false

  @Input() duringRide: boolean = true
  @Input() rideId: string = '';

  constructor(private rideService: RideService, private geolocationService: GeolocationService, private cdr: ChangeDetectorRef){}

  ngOnInit(): void {
      this.role = localStorage.getItem('user_type') || ''
  }

  shouldShowButton(): boolean {
      if (this.role === 'REGISTERED_USER' && !this.duringRide) {
          return true;
      }
      if (this.role === 'DRIVER') {
          return true;
      }
      return false;
  }

  get buttonLabel(): string {
      if (this.role === 'DRIVER' && this.duringRide) {
          return 'Stop Ride';
      }
      return 'Cancel Ride';
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

  submitStop(){
    this.isLoading = true

    const stopRideData: StopRideRequestDTO = new StopRideRequestDTO()
    
    stopRideData.getLocation(() => {
      stopRideData.endTime = new Date().toISOString()

      this.rideService.stopRideRequest(this.rideId, stopRideData).subscribe({
        next: (response) => {
          this.stoppedRideData = response;  
          this.showPopup = false
          this.showSuccessPopup = true;
          this.isLoading = false
          this.cdr.detectChanges()
        },
        error: (e) => {
          this.showPopup = false
          this.isLoading = false
          alert("Error stopping ride");
          this.cdr.detectChanges()
        }
      })
    })
  }
}
