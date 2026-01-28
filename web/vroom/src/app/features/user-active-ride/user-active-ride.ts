import { Component } from '@angular/core';
import {OnInit} from '@angular/core';
import {RideService} from '../../core/services/ride.service'
import {GetRideResponseDTO} from '../../core/models/ride/responses/get-ride-response.dto'
import {CommonModule} from '@angular/common'
import {ChangeDetectorRef} from '@angular/core'
import { CancelRide } from '../cancel-ride/cancel-ride';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
  selector: 'app-user-active-ride',
  imports: [CommonModule, CancelRide],
  templateUrl: './user-active-ride.html',
  styleUrl: './user-active-ride.css',
})
export class UserActiveRide implements OnInit {
  ride: GetRideResponseDTO | null = null;
  isLoading : boolean = true;

  constructor(
    private rideService : RideService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ){}

  ngOnInit(){
    this.loadData();
    console.log(this.ride);
  }

  loadData(){
    this.isLoading = true;
    this.rideService.getUserRide().subscribe({
      next: (data: GetRideResponseDTO) => {
        this.ride = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error fetching active ride:', err);
        this.isLoading = false;
      }
    });
  }

  onTrackRide(){
    this.router.navigate(['/ride-duration'], {queryParams: {rideID: this.ride?.rideID}});
  }

}
