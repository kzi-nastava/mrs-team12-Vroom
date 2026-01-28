import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { StoppedRideResponseDTO } from '../../core/models/ride/responses/stopped-ride-response.dto';
import { RideService } from '../../core/services/ride.service';
import { GeolocationService } from '../../core/services/geolocation.service';
import { StopRideRequestDTO } from '../../core/models/ride/requests/stop-ride-req.dto';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgToastService } from 'ng-angular-popup';
import { ActivatedRoute } from '@angular/router';

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

  private getCETDate(): string{
    const now = new Date()

    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0')
    const day = String(now.getDate()).padStart(2, '0')
    const hours = String(now.getHours()).padStart(2, '0')
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0')

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }

  submitStop(){
      this.isLoading = true
  
      const stopRideData: StopRideRequestDTO = new StopRideRequestDTO()
      
      stopRideData.getLocation(() => {
        stopRideData.endTime = this.getCETDate()
  
        this.rideService.stopRideRequest(this.rideId, stopRideData).subscribe({
          next: (response) => {
            this.stoppedRideData = {
              ...response,
              startTime: new Date(response.startTime.toString()),
              endTime: new Date(response.endTime.toString())
            }
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
      },
      (err: string)=>{
          this.toastService.danger(
              err, 
              'Failed', 
              5000, 
              true, 
              true, 
              false
          )
          this.isLoading = false
          this.cdr.detectChanges()
      })
    }

}
