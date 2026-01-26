import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import {FormsModule} from '@angular/forms'
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { firstValueFrom, Observable, forkJoin, of } from 'rxjs';
import { LoginRequestDTO } from '../../core/models/auth/requests/login-request.dto';
import { LoginResponseDTO } from '../../core/models/auth/responses/login-response.dto';
import { isHttpError } from '../../core/utils/http-error.guard';
import { ForgotPasswordRequestDTO } from '../../core/models/auth/requests/forgot-password-request.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { AuthService } from '../../core/services/auth.service';
import { DriverService } from '../../core/services/driver.service';
import { PanicNotificationService } from '../../core/services/panic-notification.service';
import { PanicService } from '../../core/services/panic.service';
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

  email: String = ''
  password: String = ''
  error: String = ''
  success: String = ''

  constructor(
    private router: Router, 
    private activatedRoute: ActivatedRoute, 
    private cdRef: ChangeDetectorRef, 
    private authService: AuthService,
    private driverService: DriverService,
    private panicService: PanicService,
    private panicNotificationService: PanicNotificationService,
    private toastService: NgToastService
  ){}

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.status = params['status'] || '';
    });
  }

  onForgotPassword(): void{
    this.isLoadingForgotPassword = true
    if(this.email === ''){
      this.error = 'Email is missing'
      this.createFailureToast('Forgot Password Failed')
      this.isLoadingForgotPassword = false
      return
    }
    
    const data: ForgotPasswordRequestDTO = {email:String(this.email).trim()}

    this.authService.createForgotPasswordRequest(data).subscribe({
      next: (response: MessageResponseDTO) => {
        this.success = response.message;  
        this.error = '';
        this.isLoadingForgotPassword = false

        this.createSuccessToast('Forgot Password Success')

        this.cdRef.detectChanges()
        setTimeout(()=>{ this.router.navigate(['/forgot-password']) }, 3000)
      },
      error: (e)=>{
        this.isLoadingForgotPassword = false
        if (e.status === 404) {
          this.error = 'User not found. Please check your email'
        }
        else if (e.status === 409) {
          this.error = 'Token is already present'
        } else if (e.status === 500) {
          this.error = 'Internal server error. Please try again later'
        } else {
          this.error = 'An unexpected error occurred. Please try again' 
        }

        this.createFailureToast('Forgot Password Failed')

        this.cdRef.detectChanges()
      }
    })
  }

  onLogin(): void{
    this.isLoadingLogin = true
    this.error=''

    if(this.email === '' || this.password === ''){
      this.error = 'Data is missing'

      this.createFailureToast('Login Failed')

      this.isLoadingLogin = false
      return
    }
    const data: LoginRequestDTO = {
      email: String(this.email).trim(),
      password: String(this.password).trim()
    }

    this.authService.createLoginRequest(data).subscribe({
      next:(response: LoginResponseDTO) =>{
        // save login response data in localstorage
        localStorage.setItem('user_type', response.type)
        localStorage.setItem('jwt', response.token)
        this.error = ''
        this.isLoadingLogin = false
        
        const connectionTasks$: Observable<void>[] = []


        if (response.type === 'DRIVER') {
          connectionTasks$.push(this.driverService.initializeWebSocket());
        }else if(response.type === 'ADMIN')
          connectionTasks$.push(this.panicNotificationService.initalizeWebSocket())

        this.cdRef.detectChanges()
        this.authService.updateStatus()

        // wait to open websocket connections 
        if (connectionTasks$.length > 0) {
    forkJoin(connectionTasks$).subscribe({
        next: () => this.router.navigate(['/']),
        error: (err) => {
            console.error('Socket initialization failed', err);
            this.router.navigate(['/']); // Still navigate or handle error
        }
    });
} else {
    // No tasks (e.g., REGISTERED_USER), redirect immediately
    this.router.navigate(['/']);
}
      },

      error: (e)=>{
        this.isLoadingLogin = false

        if (e.status === 401) {
          this.error = 'Invalid email or password' 
        } else if (e.status === 403) {
          this.error = 'Account status issue' 
        } else if (e.status === 500) {
          this.error = 'Internal server error. Please try again later' 
        } else {
          this.error = 'An unexpected error occurred. Please try again' 
        }

        this.createFailureToast('Login Failed')

        this.cdRef.detectChanges()
      }
    })
  }

  createFailureToast(title: string){
    this.toastService.danger(
      this.error.toString(),
      title,
      7000,
      true, 
      true, 
      false
    )
  }

  createSuccessToast(title: string){
    this.toastService.success(
      this.success.toString(),
      title,
      3000,
      true, 
      true, 
      false
    )
  }
}
