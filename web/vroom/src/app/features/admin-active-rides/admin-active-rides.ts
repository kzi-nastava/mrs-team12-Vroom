import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { GetActiveRideInfoDTO } from '../../core/models/admin/get-active-ride-info.dto';
import { RideService } from '../../core/services/ride.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin-active-rides',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-active-rides.html',
  styleUrl: './admin-active-rides.css',
})
export class AdminActiveRides implements OnInit {
  rides: GetActiveRideInfoDTO[] = [];
  searchText: string = '';

  constructor(
    private rideService: RideService,
    private cdr : ChangeDetectorRef,
    private router : Router
  ) {}

  ngOnInit(): void {
    this.loadActiveRides();
  }

  get filteredRides(): GetActiveRideInfoDTO[] {
    if (!this.searchText) return this.rides;
    return this.rides.filter(ride =>
      ride.driverName.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  loadActiveRides() {
    this.rideService.getActiveRides().subscribe({
      next: (data) => {
        this.rides = data;
        this.cdr.detectChanges();
      }
    });
  }

  moreInfo(id : number){
    this.router.navigate(['/ride-duration'], {queryParams: {rideID: id}});
  }
}