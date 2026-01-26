import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { StoppedRideResponseDTO } from '../../core/models/ride/responses/sopped-ride-response.dto';
import { RideService } from '../../core/services/ride.service';
import { GeolocationService } from '../../core/services/geolocation.service';
import { StopRideRequestDTO } from '../../core/models/ride/requests/stop-ride-req.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-stop-ride',
  imports: [CommonModule, FormsModule],
  templateUrl: './stop-ride.html',
  styleUrl: './stop-ride.css',
})
export class StopRide implements OnInit{
  showPopup: boolean = false
  showSuccessPopup: boolean = false
  stoppedRideData: StoppedRideResponseDTO | null = null
  isLoading: boolean = false
  role: string = ''

  @Input() rideId: string = ''

  constructor(
    private rideService: RideService, 
    private geolocationService: GeolocationService, 
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
  }

  closeSuccessPopup(){
    this.showSuccessPopup = false
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

            this.toastService.danger(
              "Failed to stop the ride. Try again.", 
              'Stopping', 
              5000, 
              true, 
              true, 
              false
            )

            this.cdr.detectChanges()
          }
        })
      })
    }

}
