import { Component } from '@angular/core';
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
  constructor(public authService: AuthService, private router: Router){}

  onLogout() {
    this.authService.logout()
    this.router.navigate(['/'])
  }
}
