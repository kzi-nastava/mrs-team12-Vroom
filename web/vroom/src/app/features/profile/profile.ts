import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {

  userRole: 'DRIVER' | 'PASSENGER' = 'DRIVER';

  firstName: string = '';
  lastName: string = '';
  address: string = '';
  phone: string = '';
  email: string = '';
  activeHoursLast24h: number = 6.5;

  vehicle = {
    brand: 'Toyota',
    model: 'Corolla',
    numberOfSeats: 4,
    licensePlate: 'NS-123-AB',
    babiesAllowed: true,
    petsAllowed: false
  };

  onChange(): void {
    console.log({
      firstName: this.firstName,
      lastName: this.lastName,
      address: this.address,
      phone: this.phone,
      email: this.email
    });
  }
}

