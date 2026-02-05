import { ChangeDetectorRef, Component } from '@angular/core';
import { RideHistoryResponseDTO } from '../../core/models/driver/ride-history-response.dto';
import { HistoryMoreInfoDTO } from '../../core/models/driver/history-more-info.dto';
import { HttpErrorResponse } from '@angular/common/http';
import { DriverService } from '../../core/services/driver.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserRideHistoryResponseDTO } from '../../core/models/ride/responses/user-ride-history-respose.dto';
import { RegisteredUserService } from '../../core/services/registered-user.service';
import { AdminService } from '../../core/services/admin.service';

@Component({
  selector: 'app-ride-history',
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-history.html',
  styleUrl: './ride-history.css',
})
export class RideHistory {
  rides: UserRideHistoryResponseDTO[] = [];
  isLoading: boolean = true;
  showPopup: boolean = false;
  selectedRide: UserRideHistoryResponseDTO | null = null;

  currentSort: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc' = 'startTime,desc';
  startDateFilter?: Date;
  endDateFilter?: Date;
  userEmailFilter?: string;

  constructor(
    public authService: AuthService,
    private userService: RegisteredUserService,
    private adminService: AdminService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadHistory();
  }

  loadHistory() {
    this.isLoading = true;

    if(this.authService.getCurrentUserType === 'REGISTERED_USER')
      this.loadUserHistory()
    else if(this.authService.getCurrentUserType === 'ADMIN')
      this.loadAllUserHistory()
    
    this.isLoading = false
    this.cdr.detectChanges;
    
    console.log(this.rides);
  }


  private loadUserHistory(){
    this.userService.getRideHistoryRequest(this.startDateFilter, this.endDateFilter, this.currentSort).subscribe({
      next: (history: UserRideHistoryResponseDTO[]) => {
        console.log(history)
        this.rides = history
      },
      error: (err) => {
          
      }
    })
  }

  private loadAllUserHistory(){
    this.adminService.getRideHistoryRequest(this.startDateFilter, this.endDateFilter, this.currentSort, this.userEmailFilter).subscribe({
      next: (history: UserRideHistoryResponseDTO[]) => {
        console.log(history)
        this.rides = history
      },
      error: (err) => {

      }
    })
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
