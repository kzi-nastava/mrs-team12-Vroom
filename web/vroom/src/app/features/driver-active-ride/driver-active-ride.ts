import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverActiveRideService } from './driver-active-ride.service';
import { Observable } from 'rxjs';
import { Ride } from './ride.model';
import { CancelRide } from '../cancel-ride/cancel-ride';
import { Router } from '@angular/router';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-driver-active-ride',
  standalone: true,
  imports: [CommonModule, CancelRide],
  templateUrl: './driver-active-ride.html',
  styleUrls: ['./driver-active-ride.css']
})
export class DriverActiveRide {
  rides$: Observable<Ride[]>;

  constructor(
    private rideService: DriverActiveRideService,
    private router: Router
  ) {
    this.rides$ = this.rideService.getActiveRides();
    this.rides$.subscribe(rides => {
      console.log('Received rides:', rides);
      rides.forEach(ride => {
        console.log(`Ride ${ride.rideID}:`, {
          isScheduled: ride.isScheduled,
          scheduledTime: ride.scheduledTime,
          startTime: ride.startTime
        });
      });
    });
  }

  startRide(rideID: number): void {
    if (!confirm('Are all passengers in the vehicle?')) return;

    this.rideService.startRide(rideID).pipe(take(1)).subscribe({
      next: () => {
        localStorage.setItem('activeRide', 'true');
        this.router.navigate(['/ride-duration'], { queryParams: { rideID } });
      },
      error: err => console.error(err)
    });
  }
}