import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverActiveRideService } from './driver-active-ride.service';
import { Observable } from 'rxjs';
import { Ride } from './ride.model';
import { Router } from '@angular/router';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-driver-active-ride',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-active-ride.html',
  styleUrls: ['./driver-active-ride.css']
})
export class DriverActiveRide {

  ride$: Observable<Ride | null>; 

  constructor(
    private rideService: DriverActiveRideService,
    private router: Router
  ) {
    this.ride$ = this.rideService.getActiveRide(); 
  }

  startRide(): void {
    if (!confirm('Are all passengers in the vehicle?')) return;

    this.ride$.pipe(take(1)).subscribe(ride => {
      if (!ride) return;

      this.rideService.startRide(ride.rideID).subscribe({
        next: () => {
          localStorage.setItem('activeRide', 'true');
          this.router.navigate(['/ride-duration'], { 
          queryParams: { rideID: ride.rideID } 
          });
        },
        error: err => console.error(err)
      });
    });
  }

  finishRide(): void {
    console.log("radi");
  }
}
