import { Component } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { PanicButton } from '../panic-btn/panic-button';
import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin, of, Subject, takeUntil } from 'rxjs';
import { RideUpdatesService } from '../../core/services/ride-update.service';
import { OnDestroy } from '@angular/core';
import { MapService } from '../../core/services/map.service';
import { StopRide } from '../stop-ride/stop-ride';
import { Router } from '@angular/router';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { ChangeDetectorRef } from '@angular/core';
import { GetRouteResponseDTO } from '../../core/models/ride/responses/get-route-response.dto';
import { PointResponseDTO } from '../../core/models/driver/point-response.dto';
import { HttpErrorResponse } from '@angular/common/http';
import { RideUpdateResponseDTO } from '../../core/models/ride/responses/ride-update-response.dto';


@Component({
  selector: 'app-ride-duration',
  imports: [CommonModule, ReactiveFormsModule, PanicButton, StopRide],
  templateUrl: './ride-duration.html',
  styleUrl: './ride-duration.css',
})
export class RideDuration implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  userRole: string | null = localStorage.getItem('user_type');
  complaintControl = new FormControl('', [Validators.required]);
  rideID: string = '';
  startAddress: string = "Loading...";
  endAddress: string = "Loading...";
  stops: string[] = [];
  eta: number = 0;
  private locationWatchId?: number;

  constructor(
    private router: Router,
    private rideService: RideService,
    private route: ActivatedRoute,
    private rideUpdatesService: RideUpdatesService,
    private mapService: MapService,
    private cdr: ChangeDetectorRef
  ) {
    this.route.queryParamMap.pipe(takeUntil(this.destroy$)).subscribe(params => {
      this.rideID = params.get('rideID') || 'unknown';
    });
  }

  ngOnInit(): void {
    console.log('Ride ID:', this.rideID);
    this.mapService.rideDurationInit(this.rideID);

    this.rideService.getRouteDetails(this.rideID).subscribe({
      next: (ride: GetRouteResponseDTO) => {
        console.log('Route details:', ride);
        this.startAddress = ride.startAddress;
        this.endAddress = ride.endAddress;
        this.cdr.detectChanges();
      }
    });

    this.rideUpdatesService.initRideUpdatesWebSocket(this.rideID).subscribe();

    if (this.userRole === 'DRIVER') {
      this.startLocationTracking();
    }

    this.rideUpdatesService.getRideUpdates()
      .pipe(takeUntil(this.destroy$))
      .subscribe((update: RideUpdateResponseDTO) => {
        if (update.status === "FINISHED") {
          const target = this.userRole === "DRIVER" ? '/driver-active-ride' : '/review';
          this.router.navigate([target], { queryParams: { rideID: this.rideID } });
        }
        this.eta = Math.round(update.timeLeft);
        this.cdr.detectChanges();
      });
  }

  private startLocationTracking(): void {
    if (!navigator.geolocation) return;
    this.locationWatchId = navigator.geolocation.watchPosition(
      position => {
        const point = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };
        this.rideUpdatesService.sendCoordinates(this.rideID, point);
      },
      error => { console.error('Error getting location', error); },
      { enableHighAccuracy: true, maximumAge: 0 }
    );
  }

  onSubmitComplaint(): void {
    const value = this.complaintControl.value?.trim();
    if (!value) return;
    console.log('Submitting complaint:', value);
    this.rideService.sendComplaintRequest(this.rideID, { complaintBody: value }).subscribe({
      next: () => this.complaintControl.reset(),
      error: (error: HttpErrorResponse) => {
          this.complaintControl.reset();
          console.error(error);}
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.locationWatchId) {
        navigator.geolocation.clearWatch(this.locationWatchId);
    }
  }

  finishRide() : void{
    this.rideService.finishRideRequest(this.rideID).subscribe({
      next:() => {
        alert('Ride finished successfully');
      }, error:() => {
        alert('There was a problem')
      }
    });
  }
}
