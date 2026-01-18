import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PanicService } from '../../core/services/panic.service';
import { Router } from '@angular/router';
import { PanicNotificationDTO } from '../../core/models/panic/responses/panic-notification.dto';

@Component({
  selector: 'app-panic-feed',
  imports: [CommonModule],
  templateUrl: './panic-feed.html',
  styleUrl: './panic-feed.css',
})
export class PanicFeed implements OnInit{
  isLoadingData: boolean = false;
  isResolving: boolean = false;
  panicAlerts: PanicNotificationDTO[] = [];

  constructor(private panicService: PanicService, private router: Router, private cdr: ChangeDetectorRef){}

  onResolved(panicID: string | number){
    this.isResolving = true
    this.panicService.resolvePanicRequest(panicID).subscribe({
        next: (response) => {
            this.isResolving = false
            this.panicAlerts = this.panicAlerts.filter(alert => alert.id !== panicID);
            this.cdr.detectChanges()
        },
        error: (err) => {
            this.isResolving = false
            alert('Could not resolve panic alert.');
            this.cdr.detectChanges()
        }
    });
  }

  ngOnInit(): void {
      // ucitati podatke u panicAlerts
      this.loadAlerts()
  }

  private loadAlerts(){
    this.isLoadingData = true
    this.panicService.getActivePanicRequests().subscribe({
      next: (response: PanicNotificationDTO[]) => {
        if(response === null){
          alert('No new alerts')
        }
        this.panicAlerts = response
        this.isLoadingData = false
        this.cdr.detectChanges()
      },
      error: (e) => {
        this.isLoadingData = false
        alert('There has been an error with loading alerts')
        this.cdr.detectChanges()
      }
    })
  }

  mapRedirect(rideID: string | number){
    this.router.navigate(['/']) // change to map and send data
  }

}
