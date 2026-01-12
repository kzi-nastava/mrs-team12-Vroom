import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PanicService } from '../../core/services/panic.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-panic-feed',
  imports: [CommonModule],
  templateUrl: './panic-feed.html',
  styleUrl: './panic-feed.css',
})
export class PanicFeed implements OnInit{
  panicAlerts = [{rideID: 1, activatedBy: 'user', time: '12-03-15'}, {rideID: 2, activatedBy: 'user', time: '12-03-15'}]

  constructor(private panicService: PanicService, private router: Router){}

  onResolved(rideID: string | number){
    console.log('resolved', rideID)
    this.panicAlerts = this.panicAlerts.filter(alert => alert.rideID != rideID)
    this.panicService.resolvePanicRequest(rideID.toString())
  }

  ngOnInit(): void {
      // ucitati podatke u panicAlerts
      this.loadAlerts()
      
  }

  private loadAlerts(){
    console.log('loading alerts....')
    // this.panicAlerts = this.panicService.getActivePanicRequests()
  }

  mapRedirect(rideID: string | number){
    this.router.navigate(['/login']) // change to map and send data
  }

}
