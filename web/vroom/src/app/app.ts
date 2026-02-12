import { ChangeDetectorRef, Component, OnDestroy, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NgToastComponent, NgToastService } from 'ng-angular-popup';
import { Subject, takeUntil } from 'rxjs';
import { ChatMessageResponseDTO } from './core/models/chat/chat-message-response.dto';
import { ChatService } from './core/services/chat.service';
import { DriverService } from './core/services/driver.service';
import { PanicNotificationService } from './core/services/panic-notification.service';
import { SocketProviderService } from './core/services/socket-provider.service';
import { Navbar } from './features/navbar/navbar';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, Navbar, NgToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {
  showNavbar = true;
  protected readonly title = signal('vroom');
  private destroy$ = new Subject<void>();

  constructor(
    private driverService: DriverService,
    private socketProvider: SocketProviderService,
    private chatService: ChatService,
    private toastService: NgToastService,
    private panicNotifService: PanicNotificationService,
    private cdr : ChangeDetectorRef
  ) {}

  ngOnInit() {
    const token = localStorage.getItem('jwt');
    const userType = localStorage.getItem('user_type');
    const userId = localStorage.getItem('user_id');

    if (token && userType) {
      this.socketProvider.initConnection()
        .pipe(takeUntil(this.destroy$))
        .subscribe(() => {
          this.setupGlobalSubscriptions(userType, userId);
          this.cdr.detectChanges();
        });
    }else{
      this.socketProvider.initConnection()
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.driverService.initializeWebSocket().subscribe();
        this.cdr.detectChanges();
      })
    }
  }

  private setupGlobalSubscriptions(userType: string, userId: string | null): void {
    if (userType === 'ADMIN') {
      this.chatService.initAdminChatWebSocket().subscribe();
      this.panicNotifService.initalizeWebSocket().subscribe();
    } else if (userType === 'REGISTERED_USER' && userId) {
      this.chatService.initUserChatWebSocket(userId).subscribe();
    } else if (userType === 'DRIVER' && userId) {
      this.chatService.initUserChatWebSocket(userId).subscribe();
      this.driverService.startTracking();
    }

    this.chatService.getMessageStream()
      .pipe(takeUntil(this.destroy$))
      .subscribe(message => {
        console.log(message)
        this.showMessageNotif(message);
      });
  }

  private showMessageNotif(message: ChatMessageResponseDTO) {
    this.toastService.info(
      message.content,
      `Message from ${message.senderName}`,
      5000,
      true,
      true,
      false
    );
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.socketProvider.disconnect();
  }
}