import { Component, Input, OnInit, ChangeDetectorRef } from '@angular/core';
import {FormsModule} from '@angular/forms'
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { LoginService } from './login.service';
import { firstValueFrom } from 'rxjs';
import { LoginRequestDTO } from '../../core/models/auth/requests/login-request.dto';
import { LoginResponseDTO } from '../../core/models/auth/responses/login-response.dto';
import { isHttpError } from '../../core/utils/http-error.guard';
import { ForgotPasswordRequestDTO } from '../../core/models/auth/requests/forgot-password-request.dto';
import { MessageResponseDTO } from '../../core/models/message-response.dto';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login implements OnInit {
  status: string = '';

  email: String = ''
  password: String = ''
  error: String = ''
  success: String = ''


  constructor(private router: Router, private activatedRoute: ActivatedRoute, private cdRef: ChangeDetectorRef, private loginService: LoginService){}

  ngOnInit(): void {
  this.activatedRoute.queryParams.subscribe(params => {
    this.status = params['status'] || '';
  });
}

  async onForgotPassword(): Promise<void>{
    if(this.email === ''){
      this.error = 'Email is missing'
      return
    }
    
    const data: ForgotPasswordRequestDTO = {email:String(this.email).trim()}

    try{
      const response: MessageResponseDTO = await firstValueFrom(this.loginService.createForgotPasswordRequest(data))
      this.success = response.message;  

      setTimeout(()=>{ this.router.navigate(['/forgot-password']) }, 3000)

    }catch(e){
        if(isHttpError(e)){
          if (e.status === 404) {
            this.error = 'User not found. Please check your email'
          } else if (e.status === 409) {
            this.error = 'Token is already present'
          } else if (e.status === 500) {
            this.error = 'Internal server error. Please try again later'
          } else {
            this.error = 'An unexpected error occurred. Please try again' 
          }
        }
    }finally{
      this.cdRef.detectChanges()
    }
    
  }

  async onLogin(): Promise<void>{
    this.error=''

    if(this.email === '' || this.password === ''){
      this.error = 'Data is missing'
      return
    }

    const data: LoginRequestDTO = {
      email: String(this.email).trim(),
      password: String(this.password).trim()
    }

    try{
      const response: LoginResponseDTO = await firstValueFrom(this.loginService.createLoginRequest(data))
      // save id and type in storage
      localStorage.setItem('user_id', String(response.userID).trim())
      localStorage.setItem('user_type', response.type)
      // redirect now to main
      this.router.navigate(['/'])
    }catch(e){
      if(isHttpError(e))
        if (e.status === 401) {
          this.error = 'Invalid email or password' 
        } else if (e.status === 403) {
          this.error = 'Account status issue' 
        } else if (e.status === 500) {
          this.error = 'Internal server error. Please try again later' 
        } else {
          this.error = 'An unexpected error occurred. Please try again' 
        }
    }finally{
      this.cdRef.detectChanges()
    }
  }
}
