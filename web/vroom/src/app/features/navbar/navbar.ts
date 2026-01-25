import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { ChangeDriverStatus } from '../change-driver-status/change-driver-status';
import { DriverService } from '../../core/services/driver.service';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
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
    private panicNotificationService: PanicNotificationService,
    private panicService: PanicService
  ){}

  onLogout() {
    this.authService.logout().subscribe({
        next: async () => {
          this.driverService.disconnectWebSocket();
          await this.panicNotificationService.disconnectWebSocket()
          await this.panicService.disconnectWebSocket()
          this.cdRef.detectChanges()  
          this.authService.updateStatus()
          this.router.navigate(['/']);
        },
        error: async () => {
            this.driverService.disconnectWebSocket();
            await this.panicNotificationService.disconnectWebSocket()
            await this.panicService.disconnectWebSocket()
            this.authService.updateStatus()
            this.cdRef.detectChanges()  
            this.router.navigate(['/']);
        }
    });
}
}
