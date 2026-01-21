import { Component } from '@angular/core';
import { RideService } from '../../core/services/ride.service';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { PanicButton } from '../panic-btn/panic-button';

@Component({
  selector: 'app-ride-duration',
  imports: [ReactiveFormsModule, PanicButton],
  templateUrl: './ride-duration.html',
  styleUrl: './ride-duration.css',
})
export class RideDuration {

  complaintControl = new FormControl('', [Validators.required]);

  constructor(private rideService: RideService) {}

  onSubmitComplaint(): void {
    const value = this.complaintControl.value?.trim();
    if (!value) return;

    this.rideService.sendComplaintRequest('1', { complaint: value }).subscribe({
      next: () => this.complaintControl.reset(),
      error: (error) => {
          this.complaintControl.reset();
          console.error(error);}
    });
  }
}
