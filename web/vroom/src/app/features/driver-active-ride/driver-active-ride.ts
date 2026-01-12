import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-driver-active-ride',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-active-ride.html',
  styleUrl: './driver-active-ride.css'
})
export class DriverActiveRide {

  ride: any = {
    id: 1,
    startAddress: 'Novi Sad',
    endAddress: 'Beograd',
    status: 'ACCEPTED', // ACCEPTED | STARTED
    price: 2300,
    passengers: [
      { firstName: 'Marko', lastName: 'Petrović' },
      { firstName: 'Ana', lastName: 'Jovanović' }
    ],
    vehicle: {
      brand: 'Toyota',
      model: 'Corolla',
      licensePlate: 'NS-123-AB'
    }
  };

  startRide(): void {
    if (confirm('Are all passengers in the vehicle?')) {
      // TODO: call backend
      this.ride.status = 'STARTED';
    }
  }

  finishRide(): void {
    if (confirm('Finish the ride?')) {
      // TODO: call backend
      this.ride = null;
    }
  }
}