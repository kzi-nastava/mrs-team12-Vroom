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
  startAddress: string = "Start Address";
  endAddress: string = "End Address";
  stops: string[] = [];
  eta: number = 0;

  constructor(
    private router: Router,
    private rideService: RideService,
    private route: ActivatedRoute,
    private rideUpdatesService: RideUpdatesService,
    private mapService: MapService
  ) {
    this.userRole = localStorage.getItem('user_type');
    console.log('User role:', this.userRole);
    this.route.queryParamMap.subscribe(params => {
      this.rideID = params.get('rideID') || '1';
    });
    
  }

  ngOnInit(): void {
    this.mapService.rideDurationInit(this.rideID);
    this.rideService.getRouteDetails(this.rideID).subscribe({
      next: (ride) => {
        this.fetchAddresses(ride);
      }
    });
    if (!this.rideUpdatesService.stompClient || !this.rideUpdatesService.stompClient.connected){
      this.rideUpdatesService.initRideUpdatesWebSocket(this.rideID);
    }
    this.rideUpdatesService.getRideUpdates().subscribe({
      next: (update) => {
        this.eta = Math.round(update.timeLeft);
        console.log('Ride Update:', update);
      }
    });
  }

  private fetchAddresses(ride: any): void {
    const start$ = this.rideService.getAddress(ride.startLocationLat, ride.startLocationLng);
    const end$ = this.rideService.getAddress(ride.endLocationLat, ride.endLocationLng);
    const stops$ = ride.stops?.length > 0 
      ? forkJoin(ride.stops.map((s: any) => this.rideService.getAddress(s.lat, s.lng)))
      : of([]);

    forkJoin([start$, end$, stops$]).subscribe({
      next: ([start, end, stops]) => {
        this.startAddress = start;
        this.endAddress = end;
        this.stops = stops as string[];
      }
    });
  }

  onSubmitComplaint(): void {
    const value = this.complaintControl.value?.trim();
    if (!value) return;

    this.rideService.sendComplaintRequest('1', { complaint: value }).subscribe({
      next: () => this.complaintControl.reset(),
      error: (error) => {
          this.complaintControl.reset();
          console.error(error);}
    });
  }

  ngOnDestroy(): void {
    this.rideUpdatesService.disconnectRideUpdatesWebSocket();
  }

  finishRide() : void{

    this.rideService.finishRideRequest('1').subscribe({
      next:(response: MessageResponseDTO) => {
        // implement new dto for scheduled ride from server
        // if response is empty no scheduled rides redirect to main page 
        // else load scheduled ride data
        alert('Ride finished successfully');
      }, error:(err) => {
        alert('There was a problem')
      }
    });
    
    this.router.navigate(['/'])
  }
}
