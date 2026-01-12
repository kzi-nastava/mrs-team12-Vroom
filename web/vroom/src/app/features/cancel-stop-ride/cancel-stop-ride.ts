import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { GeolocationService } from '../../core/services/geolocation.service';
import { FormsModule } from '@angular/forms';

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
  reason: String = ''

  @Input() duringRide: boolean = false

  constructor(private rideService: RideService, private geolocationService: GeolocationService, private cdr: ChangeDetectorRef){}

  ngOnInit(): void {
      this.role = localStorage.getItem('user_type') || ''
  }



  shouldShowButton(): boolean {
      if (this.role === 'registeredUser' && !this.duringRide) {
          return true;
      }
      if (this.role === 'driver') {
          return true;
      }
      return false;
  }

  get buttonLabel(): string {
      if (this.role === 'driver' && this.duringRide) {
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

  private currentLocation(){

  }

  submitCancellation(){
    console.log('cancelling')
    console.log(this.reason)
  }

  submitStop(){
    console.log('stopping')
  }
}
