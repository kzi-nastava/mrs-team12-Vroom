import { Component } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { PanicButton } from '../panic-btn/panic-button';
import { OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin, of } from 'rxjs';
import { RideUpdatesService } from '../../core/services/ride-update-service';
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
    this.userRole = localStorage.getItem('user_type');
    console.log('User role:', this.userRole);
    this.route.queryParamMap.subscribe(params => {
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
        this.fetchAddresses(ride);
        this.cdr.detectChanges();
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error fetching route details:', error);
      }
    });

    if (!this.rideUpdatesService.stompClient || !this.rideUpdatesService.stompClient.connected){
      this.rideUpdatesService.initRideUpdatesWebSocket(this.rideID);
    }
    if (this.userRole === 'DRIVER') {
      this.startLocationTracking();
    }
    

    this.rideUpdatesService.getRideUpdates().subscribe({
      next: (update: RideUpdateResponseDTO) => {
        if (update.status == "FINISHED"){
          if (this.userRole === "DRIVER"){
            this.router.navigate(['/driver-active-ride']);
          }
          if (this.userRole === "REGISTERED_USER"){
            this.router.navigate(['/review'], {queryParams: {rideID: this.rideID}});
          }
        }
        this.eta = Math.round(update.timeLeft);
        console.log('Ride Update:', update);
        this.cdr.detectChanges();
      }
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
      error => {
        console.error('Error getting location', error); 
      },
      {
        enableHighAccuracy: true,
        maximumAge: 0
      }
    );
  }

  private fetchAddresses(ride: GetRouteResponseDTO): void {
    const stops$ = ride.stops?.length > 0 
      ? forkJoin(ride.stops.map((s: PointResponseDTO) => this.rideService.getAddress(s.lat, s.lng)))
      : of([]);

    forkJoin([stops$]).subscribe({
      next: ([stops]) => {
        this.stops = stops as string[];
        this.cdr.detectChanges();
      }
    });
  }

  onSubmitComplaint(): void {
    const value = this.complaintControl.value?.trim();
    if (!value) return;
    console.log('Submitting complaint:', value);
    this.rideService.sendComplaintRequest(this.rideID, { complaint: value }).subscribe({
      next: () => this.complaintControl.reset(),
      error: (error: HttpErrorResponse) => {
          this.complaintControl.reset();
          console.error(error);}
    });
  }

  ngOnDestroy(): void {
    if (this.locationWatchId) {
      navigator.geolocation.clearWatch(this.locationWatchId);
    }
    this.rideUpdatesService.disconnectRideUpdatesWebSocket();
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
