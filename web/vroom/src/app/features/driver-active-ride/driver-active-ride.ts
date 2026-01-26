import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverActiveRideService } from './driver-active-ride.service';
import { Observable } from 'rxjs';
import { Ride } from './ride.model';

@Component({
  selector: 'app-driver-active-ride',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-active-ride.html',
  styleUrls: ['./driver-active-ride.css']
})
export class DriverActiveRide {

  ride$: Observable<Ride | null>; 

  constructor(private rideService: DriverActiveRideService) {
    this.ride$ = this.rideService.getActiveRide(); 
  }

  startRide(): void {
    if (!confirm('Are all passengers in the vehicle?')) return;

    this.rideService.startRide().subscribe({
      next: () => {
  
        this.ride$ = this.rideService.getActiveRide();
      },
      error: err => console.error(err)
    });
  }

  finishRide(): void {
    console.log("radi");
  }
}
