import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { forkJoin, Observable, of } from 'rxjs';
import { ForgotPasswordRequestDTO } from '../../core/models/auth/requests/forgot-password-request.dto';
import { LoginRequestDTO } from '../../core/models/auth/requests/login-request.dto';
import { LoginResponseDTO } from '../../core/models/auth/responses/login-response.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { AuthService } from '../../core/services/auth.service';
import { ChatService } from '../../core/services/chat.service';
import { DriverService } from '../../core/services/driver.service';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { SocketProviderService } from '../../core/services/socket-provider.service';
import { NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  status: string = '';
  isLoadingLogin: boolean = false;
  isLoadingForgotPassword: boolean = false;
  email: string = '';
  password: string = '';
  error: string = '';
  success: string = '';

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private cdRef: ChangeDetectorRef,
    private authService: AuthService,
    private driverService: DriverService,
    private panicNotificationService: PanicNotificationService,
    private toastService: NgToastService,
    private chatService: ChatService,
    private socketProvider: SocketProviderService
  ) {}

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.status = params['status'] || '';
    });
  }

  onForgotPassword(): void {
    this.isLoadingForgotPassword = true;
    if (this.email === '') {
      this.error = 'Email is missing';
      this.createFailureToast('Forgot Password Failed');
      this.isLoadingForgotPassword = false;
      return;
    }

    const data: ForgotPasswordRequestDTO = { email: String(this.email).trim() };

    this.authService.createForgotPasswordRequest(data).subscribe({
      next: (response: MessageResponseDTO) => {
        this.success = response.message;
        this.error = '';
        this.isLoadingForgotPassword = false;
        this.createSuccessToast('Forgot Password Success');
        this.cdRef.detectChanges();
        setTimeout(() => { this.router.navigate(['/forgot-password']); }, 3000);
      },
      error: (e: HttpErrorResponse) => {
        this.isLoadingForgotPassword = false;
        if (e.status === 404) this.error = 'User not found. Please check your email';
        else if (e.status === 409) this.error = 'Token is already present';
        else if (e.status === 500) this.error = 'Internal server error. Please try again later';
        else this.error = 'An unexpected error occurred. Please try again';

        this.createFailureToast('Forgot Password Failed');
        this.cdRef.detectChanges();
      }
    });
  }

onLogin(): void {
  this.isLoadingLogin = true;
  this.error = '';

  if (this.email === '' || this.password === '') {
    this.error = 'Data is missing';
    this.createFailureToast('Login Failed');
    this.isLoadingLogin = false;
    return;
  }

  const data: LoginRequestDTO = {
    email: String(this.email).trim(),
    password: String(this.password).trim()
  };

  this.authService.createLoginRequest(data).subscribe({
    next: (response: LoginResponseDTO) => {
      localStorage.setItem('user_type', response.type);
      localStorage.setItem('jwt', response.token);
      localStorage.setItem('user_id', response.userId.toString());
      this.isLoadingLogin = false;

      this.socketProvider.initConnection().subscribe({
        next: () => {
          let tasks$: Observable<any> = of(undefined);

          if (response.type === "ADMIN") {            
            tasks$ = forkJoin([
            this.panicNotificationService.initalizeWebSocket(),
            this.chatService.initAdminChatWebSocket()
            ]);
          } else if (response.type === "REGISTERED_USER") {
            tasks$ = forkJoin([
              this.driverService.initializeWebSocket(),
              this.chatService.initUserChatWebSocket(response.userId.toString())
            ]);
          } else if (response.type === "DRIVER") {
            tasks$ = forkJoin([
              this.driverService.initializeWebSocket(),
              this.chatService.initUserChatWebSocket(response.userId.toString())
            ]);
          }

          tasks$.subscribe(() => {
            const path = response.type === 'ADMIN' ? '/admin' : '';
            this.router.navigate([path]);
          });
        },
        error: (err) => {
          console.error("Connection failed", err);
          this.router.navigate(['/']);
        }
      });
      this.authService.updateStatus();
      this.cdRef.detectChanges();
    },
    error: (e: HttpErrorResponse) => {
      this.isLoadingLogin = false;
      this.error = e.status === 401 ? 'Invalid email or password' :
                   e.status === 403 ? 'Account status issue' :
                   e.status === 500 ? 'Internal server error' : 'Unexpected error';
      this.createFailureToast('Login Failed');
      this.cdRef.detectChanges();
    }
  });
}

  createFailureToast(title: string) {
    this.toastService.danger(this.error.toString(), title, 7000, true, true, false);
  }

  createSuccessToast(title: string) {
    this.toastService.success(this.success.toString(), title, 3000, true, true, false);
  }
}