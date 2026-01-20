// review-popup.component.ts
import { MessageResponseDTO } from "../../core/models/message-response.dto";
import { LeaveReviewRequestDTO } from '../../core/models/ride/requests/leave-review-req.dto'
import { RideService } from '../../core/services/ride.service'
import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-review-popup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './review-ride-form.html',
  styleUrls: ['./review-ride-form.css']
})
export class ReviewPopup {
  @Output() closePopup = new EventEmitter<void>();
  @Output() submitReview = new EventEmitter<{
    driverRating: number;
    carRating: number;
    comment: string;
  }>();

  driverRating: number = 0;
  carRating: number = 0;
  comment: string = '';

  constructor(private rideService : RideService){};

  setDriverRating(rating: number): void {
    this.driverRating = rating;
  }

  setCarRating(rating: number): void {
    this.carRating = rating;
  }

  onSubmit(): void {
    if (this.driverRating === 0 || this.carRating === 0) {
      alert('Please rate both the driver and the car');
      return;
    }

    const data: LeaveReviewRequestDTO = {
      driverRating: this.driverRating,
      vehicleRating: this.carRating,
      comment: this.comment
    }

    //IMPORTANT!!!!! hardcoded ride id needs to be changed
    this.rideService.leaveReviewRequest('1', data).subscribe({
      next:(response: MessageResponseDTO) => {
        alert('Review submitted successfully');
      }, error:(err) => {
        alert("Review couldn't be sent.");
        this.onClose;
      }
    });




    this.driverRating = 0;
    this.carRating = 0;
    this.comment = '';
    this.onClose();
  }
  

  onClose(): void {
    this.closePopup.emit();
  }
}