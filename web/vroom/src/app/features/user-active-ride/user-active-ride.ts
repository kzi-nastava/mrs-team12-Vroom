import { Component } from '@angular/core';
import {OnInit} from '@angular/core';
import {RideService} from '../../core/services/ride.service'
import {GetRideResponseDTO} from '../../core/models/ride/responses/get-ride-response.dto'
import {CommonModule} from '@angular/common'
import {ChangeDetectorRef} from '@angular/core'
import { CancelRide } from '../cancel-ride/cancel-ride';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { UserActiveRideResponseDTO } from '../../core/models/ride/responses/user-active-ride-response.dto';


@Component({
  selector: 'app-user-active-ride',
  imports: [CommonModule, CancelRide],
  templateUrl: './user-active-ride.html',
  styleUrl: './user-active-ride.css',
})
export class UserActiveRide implements OnInit {
  rides: UserActiveRideResponseDTO[] = [];
  isLoading : boolean = true;

  constructor(
    private rideService : RideService,
    private cdr: ChangeDetectorRef,
    private router: Router
  ){}

  ngOnInit(){
    this.loadData();
  }

  loadData(){
    this.isLoading = true;
    this.rideService.getUserRide().subscribe({
      next: (data: UserActiveRideResponseDTO[]) => {
        this.rides = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => {
        console.error('Error fetching active ride:', err);
        this.isLoading = false;
      }
    });
  }

  onTrackRide(rideID : number){
    this.router.navigate(['/ride-duration'], {queryParams: { rideID }});
  }

}
