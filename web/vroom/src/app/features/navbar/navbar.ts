import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { ChangeDriverStatus } from '../change-driver-status/change-driver-status';
import { DriverService } from '../../core/services/driver.service';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
import { forkJoin, Observable } from 'rxjs';
import { ChatService } from '../../core/services/chat.service';
import { SocketProviderService } from '../../core/services/socket-provider.service';
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
    private cdRef: ChangeDetectorRef
  ){}


  onLogout() {
    this.authService.logout().subscribe({
        next: () => {
          this.finalizeLogout()
        },
        error: () => {
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
