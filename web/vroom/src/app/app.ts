import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './features/navbar/navbar';
import { NgToastComponent } from 'ng-angular-popup';
import { AuthService } from './core/services/auth.service';
import { OnInit, OnDestroy } from '@angular/core';
import { DriverService } from './core/services/driver.service';
import { PanicService } from './core/services/panic.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, NgToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {


  showNavbar = true;
  protected readonly title = signal('vroom');

  constructor(private authService: AuthService, private driverService: DriverService, private panicService: PanicService) {}

  ngOnInit() {
    if (this.authService.isLoggedIn && this.authService.getCurrentUserType === 'DRIVER') {
      this.driverService.initializeWebSocket();
      this.driverService.startTracking();
    }
  }

  ngOnDestroy() {
    this.driverService.disconnectWebSocket();
  }

}
