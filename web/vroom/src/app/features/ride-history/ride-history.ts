import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RideHistoryResponseDTO } from '../../core/models/driver/ride-history-response.dto';
import { HistoryMoreInfoDTO } from '../../core/models/driver/history-more-info.dto';
import { HttpErrorResponse } from '@angular/common/http';
import { DriverService } from '../../core/services/driver.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { RideResponseDTO } from '../../core/models/ride/responses/ride-respose.dto';
import { RegisteredUserService } from '../../core/services/registered-user.service';
import { AdminService } from '../../core/services/admin.service';
import { MapService } from '../../core/services/map.service';
import { ReviewPopup } from '../review-ride-form/review-ride-form';
import { RideService } from '../../core/services/ride.service';
import { NgToastService } from 'ng-angular-popup';
import { ActivatedRoute } from '@angular/router';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-ride-history',
  imports: [CommonModule, FormsModule, ReviewPopup],
  templateUrl: './ride-history.html',
  styleUrl: './ride-history.css',
})
export class RideHistory implements OnInit {
  rides: RideResponseDTO[] = [];
  isLoading: boolean = true;
  showPopup: boolean = false;
  selectedRide: RideResponseDTO | null | undefined = null;

  currentSort: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc' = 'startTime,desc';
  startDateFilter?: Date;
  endDateFilter?: Date;
  userEmailFilter?: string;
  private emailFilterSubject = new Subject<string | undefined>();

  isReviewFormShowed: boolean = false

  pageNum: number = 0
  pageSize: number = 10
  nextPageAvailable: boolean = true
  backPageAvailable: boolean = false

  constructor(
    public authService: AuthService,
    private userService: RegisteredUserService,
    private adminService: AdminService,
    private mapService: MapService,
    private rideService: RideService,
    private toastService: NgToastService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.userEmailFilter = this.route.snapshot.queryParamMap.get('email') ?? undefined;
    
    this.emailFilterSubject.pipe(
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(value => {
      this.userEmailFilter = value
      this.pageNum = 0
      this.loadHistory()
    })

    this.loadHistory();
  }

  loadHistory() {
    this.isLoading = true;

    if(this.authService.getCurrentUserType === 'REGISTERED_USER')
      this.loadUserHistory()
    else if(this.authService.getCurrentUserType === 'ADMIN')
      this.loadAllUserHistory()
    
  }


  private loadUserHistory(){
    this.userService.getRideHistoryRequest(
      this.pageNum,
      this.pageSize,
      this.startDateFilter, 
      this.endDateFilter, 
      this.currentSort
    ).subscribe({
      next: (history: RideResponseDTO[]) => {
        console.log(history)
        this.rides = history

        this.nextPageAvailable = history.length === this.pageSize;
        this.backPageAvailable = this.pageNum > 0;

        this.isLoading = false
        this.cdr.detectChanges()
      },
      error: (err) => {
          this.rides = []
          this.isLoading = false
          this.cdr.detectChanges()
      }
    })
  }

  private loadAllUserHistory(){
    this.adminService.getRideHistoryRequest(
      this.pageNum, 
      this.pageSize,
      this.startDateFilter, 
      this.endDateFilter, 
      this.currentSort, 
      this.userEmailFilter
    ).subscribe({
      next: (history: RideResponseDTO[]) => {
        console.log(history)
        this.rides = history

        this.nextPageAvailable = history.length === this.pageSize;
        this.backPageAvailable = this.pageNum > 0;

        this.isLoading = false
        this.cdr.detectChanges()
      },
      error: (err) => {
          this.rides = []
          this.isLoading = false
          this.cdr.detectChanges()
      }
    })
  }

  onEmailFilterChange(value: string) {
    const filterValue = value.trim() === '' ? undefined : value.trim();
    this.emailFilterSubject.next(filterValue);
  }


  onSortChange(event: Event) : void {
    const select = event.target as HTMLSelectElement;
    this.currentSort = select.value as 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc';
    this.loadHistory();
  }

  onFilterChange() : void {
    this.pageNum = 0
    this.loadHistory();
  }

  clearFilters() : void {
    this.startDateFilter = undefined;
    this.endDateFilter = undefined;
    this.userEmailFilter = undefined

    this.loadHistory();

    this.cdr.detectChanges();
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

  openPopup(rideId : number){
    this.selectedRide = this.rides.find((ride) => ride.rideId == rideId)
    this.showPopup = true
  }

  closePopup(){
    this.showPopup = false;
    this.selectedRide = null;
    this.cdr.detectChanges();
  }

  getStars(rating : number){
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  }

  showOnMapRoute(rideId: number){
    const foundRide = this.rides.find((ride) => ride.rideId === rideId)
    if(!foundRide){
      return
    }

    const start = {
      lat: foundRide.route.startLocationLat,
      lng: foundRide.route.startLocationLng
    }

    const end = {
      lat: foundRide.route.endLocationLat,
      lng: foundRide.route.endLocationLng
    }

    const stops = foundRide.route.stops

    const payload = {
      start: start,
      end: end,
      stops: stops
    }

    this.mapService.drawRoute(payload)
  }

  canLeaveReview(startTime: string | Date): boolean{
    const rideDate = new Date(startTime).getTime()
    const now = new Date().getTime()

    const threeDaysMs = 3 * 24 * 60 * 60 * 1000;

    return (now - rideDate) < threeDaysMs;
  }

  
  openLeaveReview(){
    this.isReviewFormShowed = true
  }

  closeLeaveReview(){
    this.isReviewFormShowed = false

    this.rideService.getRide(this.selectedRide?.rideId).subscribe({
      next: (updatedRide: RideResponseDTO) => {
        this.selectedRide = updatedRide
        this.rides = this.rides.map((ride) => ride.rideId === updatedRide.rideId ? updatedRide : ride)
        this.cdr.detectChanges()
      },
      error: (err) => {
          this.createFailureToast('Server error', 'Unable to get new ride data')
          this.cdr.detectChanges()
      }
    })
  }

  

  nextPage(){
    if(this.rides.length < 10){
      this.nextPageAvailable = false
      return
    }
    
    this.pageNum++
    this.loadHistory()
  }

  backPage(){
    if(this.pageNum == 0){
      this.backPageAvailable = false
      return
    }
    
    this.pageNum--
    this.loadHistory()
  }

  private createFailureToast(title: string, desc: string){
    this.toastService.danger(
      title,
      desc,
      7000,
      true, 
      true, 
      false
    )
  }
}
