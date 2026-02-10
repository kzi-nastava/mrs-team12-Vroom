import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './features/navbar/navbar';
import { NgToastComponent } from 'ng-angular-popup';
import { AuthService } from './core/services/auth.service';
import { OnInit, OnDestroy } from '@angular/core';
import { DriverService } from './core/services/driver.service';
import { PanicService } from './core/services/panic.service';
import { SocketProviderService } from './core/services/socket-provider.service';
import { ChatService } from './core/services/chat.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, NgToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {


  showNavbar = true;
  protected readonly title = signal('vroom');

  constructor(
    private authService: AuthService,
    private driverService: DriverService, 
    private panicService: PanicService,
    private socketProvider : SocketProviderService,
    private chatService : ChatService
  ) {}

  ngOnInit() {
    // if (this.authService.isLoggedIn && this.authService.getCurrentUserType === 'DRIVER') {
    //   this.driverService.initializeWebSocket();
    //   this.driverService.startTracking();
    // }

    const token = localStorage.getItem('jwt');
    const userType = localStorage.getItem('user_type');
    const userId = localStorage.getItem('user_id');

    if (token && userType) {
      this.socketProvider.initConnection().subscribe({
        next: () => {
          if (userType === 'ADMIN') {
            this.chatService.initAdminChatWebSocket().subscribe();
          } else if (userType === 'REGISTERED_USER' && userId) {
            this.chatService.initUserChatWebSocket(userId).subscribe();
          }
        }
      });
    }
  }

  ngOnDestroy() {
    this.driverService.disconnectWebSocket();
  }

}
