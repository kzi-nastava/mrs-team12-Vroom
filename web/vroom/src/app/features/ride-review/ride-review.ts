import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ride-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-review.html',
  styleUrls: ['./ride-review.css']
})

export class RideReview {
  driverRating: number = 0;
  carRating: number = 0;
  comment: string = '';

  setDriverRating(rating: number): void {
    this.driverRating = rating;
  }

  setCarRating(rating: number): void {
    this.carRating = rating;
  }

  submitReview(): void {
    if (this.driverRating === 0 || this.carRating === 0) {
      alert('Please rate both the driver and the car');
      return;
    }

    console.log('Review submitted:', {
      driverRating: this.driverRating,
      carRating: this.carRating,
      comment: this.comment
    });
    
    alert('Review submitted successfully!');
    
    // Reset form
    this.driverRating = 0;
    this.carRating = 0;
    this.comment = '';
  }
}