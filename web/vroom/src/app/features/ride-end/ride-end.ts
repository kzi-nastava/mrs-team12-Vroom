// ride-review.component.ts
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReviewPopup } from '../review-ride-form/review-ride-form';


@Component({
  selector: 'app-ride-review',
  standalone: true,
  imports: [CommonModule, FormsModule, ReviewPopup],
  templateUrl: './ride-end.html',
  styleUrls: ['./ride-end.css']
})
export class RideEnd {
  showReviewPopup: boolean = false;

  openReviewForm(): void {
    this.showReviewPopup = true;
  }

  closeReviewForm(): void {
    this.showReviewPopup = false;
  }
  

}