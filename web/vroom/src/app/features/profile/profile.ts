import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../profile/profile.service'; // ← PUTANJA!

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {

  userRole: 'DRIVER' | 'PASSENGER' = 'PASSENGER';

  firstName = '';
  lastName = '';
  address = '';
  phone = '';
  email = '';

  vehicle: any;
  activeHoursLast24h = 0;

  constructor(private profileService: ProfileService) {}

ngOnInit(): void {
  this.profileService.getMyProfile().subscribe(profile => {
    this.firstName = profile.firstName;
    this.lastName = profile.lastName;
    this.address = profile.address;
    this.phone = profile.phoneNumber;
    this.email = profile.email;

    if (profile.vehicle) {
      this.userRole = 'DRIVER';
      this.vehicle = profile.vehicle;
      this.activeHoursLast24h = profile.activeHoursLast24h ?? 0;
    }
  });
}

fillProfile(profile: any) {
  this.firstName = profile.firstName;
  this.lastName = profile.lastName;
  this.address = profile.address;
  this.phone = profile.phone;
  this.email = profile.email;
}

onChange(): void {
  const payload = {
    firstName: this.firstName,
    lastName: this.lastName,
    address: this.address,
    phoneNumber: this.phone, 
    email: this.email
  };

  this.profileService.updateMyProfile(payload)
    .subscribe({
      next: updated => console.log('Updated profile', updated),
      error: err => console.error('Update failed', err)
    });
}
}


