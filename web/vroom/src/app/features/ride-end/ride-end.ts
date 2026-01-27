// ride-review.component.ts
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReviewPopup } from '../review-ride-form/review-ride-form';
import { OnInit } from '@angular/core'
import { RideService } from '../../core/services/ride.service'
import { ActivatedRoute } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-ride-review',
  standalone: true,
  imports: [CommonModule, FormsModule, ReviewPopup],
  templateUrl: './ride-end.html',
  styleUrls: ['./ride-end.css']
})
export class RideEnd implements OnInit {
  showReviewPopup: boolean = false;
  rideID: string = '';
  startAddress: string = "Loading...";
  endAddress: string = "Loading...";

  constructor(
    private rideService: RideService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ){
    this.route.queryParamMap.subscribe(params => {
      this.rideID = params.get('rideID') || 'unknown';
    });
  }

  ngOnInit(){
    this.rideService.getRouteDetails(this.rideID).subscribe({
      next: (ride) => {
        console.log('Route details:', ride);
        this.startAddress = ride.startAddress;
        this.endAddress = ride.endAddress;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error fetching route details:', error);
      }
    });
  }

  openReviewForm(): void {
    this.showReviewPopup = true;
  }

  closeReviewForm(): void {
    this.showReviewPopup = false;
  }
  

}