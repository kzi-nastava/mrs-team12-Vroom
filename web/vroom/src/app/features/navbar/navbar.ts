import { ChangeDetectorRef, Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';
import { ChangeDriverStatus } from '../change-driver-status/change-driver-status';
@Component({
  selector: 'app-navbar',
  imports: [RouterModule, CommonModule, ChangeDriverStatus],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  constructor(public authService: AuthService, private router: Router, private cdRef: ChangeDetectorRef){}

  onLogout() {
    this.authService.logout().subscribe({
        next: () => {
          this.cdRef.detectChanges()  
          this.router.navigate(['/']);

        },
        error: () => {
            this.cdRef.detectChanges()  
            this.router.navigate(['/']);
        }
    });
}
}
