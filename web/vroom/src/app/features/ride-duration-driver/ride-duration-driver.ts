import { Component } from '@angular/core';
import {PanicButton} from '../panic-btn/panic-button';
import {CancelStopRide} from '../cancel-stop-ride/cancel-stop-ride';
import {RideService} from '../../core/services/ride.service';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { Router, ActivatedRoute } from '@angular/router';
@Component({
  selector: 'app-ride-duration-driver',
  imports: [PanicButton, CancelStopRide],
  templateUrl: './ride-duration-driver.html',
  styleUrl: './ride-duration-driver.css',
})
export class RideDurationDriver {

  constructor(private router: Router, private rideService : RideService){};

  finishRide() : void{

    this.rideService.finishRideRequest('1').subscribe({
      next:(response: MessageResponseDTO) => {
        // implement new dto for scheduled ride from server
        // if response is empty no scheduled rides redirect to main page 
        // else load scheduled ride data
        alert('Ride finished successfully');
      }, error:(err) => {
        alert('There was a problem')
      }
    });
    
    this.router.navigate(['/'])
  }

}
