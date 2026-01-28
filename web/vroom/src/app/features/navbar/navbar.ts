import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { ChangeDriverStatus } from '../change-driver-status/change-driver-status';
import { DriverService } from '../../core/services/driver.service';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
import { forkJoin, Observable } from 'rxjs';
@Component({
  selector: 'app-navbar',
  imports: [RouterModule, CommonModule, ChangeDriverStatus],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  constructor(
    public authService: AuthService, 
    private router: Router, 
    private cdRef: ChangeDetectorRef,
    private driverService: DriverService,
    private panicNotificationService: PanicNotificationService
  ){}


  onLogout() {
    this.authService.logout().subscribe({
        next: () => {
          this.driverService.disconnectWebSocket()
          this.panicNotificationService.disconnectWebSocket()

          this.finalizeLogout()
        },
        error: () => {
            this.driverService.disconnectWebSocket()
            this.panicNotificationService.disconnectWebSocket()

          this.finalizeLogout()   
        }
    });
  }

  finalizeLogout(){
    this.authService.updateStatus()
    this.cdRef.detectChanges() 
    this.router.navigate(['/'])
  }
}
