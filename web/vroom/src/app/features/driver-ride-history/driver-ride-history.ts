import { Component } from '@angular/core';
import { DriverService } from '../../core/services/driver.service';
import { OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideHistoryResponseDTO } from '../../core/models/driver/ride-history-response.dto';
import { FormsModule } from '@angular/forms';
import { ChangeDetectorRef } from '@angular/core';
import { HistoryMoreInfoDTO } from '../../core/models/driver/history-more-info.dto';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-driver-ride-history',
  imports: [CommonModule, FormsModule],
  templateUrl: './driver-ride-history.html',
  styleUrl: './driver-ride-history.css',
})
export class DriverRideHistory implements OnInit {
  rides: RideHistoryResponseDTO[] = [];
  isLoading: boolean = true;
  showPopup: boolean = false;
  selectedRide: HistoryMoreInfoDTO | null = null;

  currentSort: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc' = 'startTime,desc';
  startDateFilter?: Date;
  endDateFilter?: Date;

  constructor(
    private driverService: DriverService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory() {
    this.isLoading = true;
    this.driverService
    .getDriverRideHistory(this.startDateFilter, this.endDateFilter, this.currentSort)
    .subscribe({
      next:( history ) => {
        console.log(history);
        this.rides = history;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching ride history:', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
    console.log(this.rides);
  }

  onSortChange(event: Event) : void {
    const select = event.target as HTMLSelectElement;
    this.currentSort = select.value as 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc';
    this.loadHistory();
  }

  onFilterChange() : void {
    this.loadHistory();
  }

  clearFilters() : void {
    this.startDateFilter = undefined;
    this.endDateFilter = undefined;
    this.loadHistory();
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'finished':
      case 'in_progress':
        return 'completed';
      case 'cancelled_by_driver':
      case 'cancelled_by_user':
      case 'denied':
        return 'cancelled';
      default:
        return '';
    }
  }

  getStatusLabel(status: string): string {
    switch (status.toLowerCase()) {
      case 'finished':
          return 'Completed';
      case 'cancelled_by_driver':
          return 'Cancelled by Driver';
      case 'cancelled_by_user':
          return 'Cancelled by User';
      case 'denied':
          return 'Denied';
      case 'in_progress':
          return 'In Progress';
      default:
          return status;
    }
  }

  openPopup(rideID : number){
    this.driverService.getRideHistoryMoreInfo(rideID.toString()).subscribe({
      next: (ride: HistoryMoreInfoDTO) => {
        this.selectedRide = ride;
        this.showPopup = true;
        this.cdr.detectChanges();
        console.log(ride);
      },
      error: (error : HttpErrorResponse) => {
        console.error('Error fetching ride details:', error);
      }
    });
  }

  closePopup(){
    this.showPopup = false;
    this.selectedRide = null;
    this.cdr.detectChanges();
  }

  getStars(rating : number){
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

}
