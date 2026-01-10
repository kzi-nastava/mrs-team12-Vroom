import { Component, ChangeDetectorRef  } from '@angular/core';
import {FormsModule} from '@angular/forms'
import { ForgotPasswordService } from './forgot-password.service';
import { firstValueFrom } from 'rxjs';
import { MessageResponseDTO } from '../../core/models/message-response.dto';
import { Router } from '@angular/router';
import { ResetPasswordRequestDTO } from '../../core/models/auth/requests/reset-password-request.dto';
import { isHttpError } from '../../core/utils/http-error.guard';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  email: String =''
  code: String =''
  password: String=''
  rePassword: String =''
  error: String=''
  success: String=''

  constructor(private forgotPasswordService: ForgotPasswordService, private router: Router, private cdRef: ChangeDetectorRef){}

  async onSubmit(): Promise<void>{
    this.error=''

    if(this.email === '' || this.code === '' || this.password === '' || this.rePassword === ''){
      this.error = 'Data is missing'
      return
    }

    const passwordInvalid = this.forgotPasswordService.isPasswordValid(this.password.toString())
    if(passwordInvalid !== null){
      this.error = passwordInvalid
      return
    }

    if(this.password !== this.rePassword){
      this.error='Password must match'
      return
    }

    const data: ResetPasswordRequestDTO = {
      email: String(this.email).trim(),
      code: String(this.code).trim(),
      password: String(this.password).trim()
    }

    try{
      const response: MessageResponseDTO = await firstValueFrom(this.forgotPasswordService.createRequest(data))
      this.success = response.message;  
      this.cdRef.detectChanges()

      setTimeout(()=>{ this.router.navigate(['/login']) }, 3000)

    }catch(e){
        if(isHttpError(e)){
          if(e.status === 400){
            this.error = 'Invalid or expired token'
          } else if (e.status === 500) {
            this.error = 'Internal server error. Please try again later'
          }else {
            this.error = 'An unexpected error occurred';
          }
        }
    }
  }
}
