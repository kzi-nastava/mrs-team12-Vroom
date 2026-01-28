import { Component, ChangeDetectorRef  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { isHttpError } from '../../core/utils/http-error.guard';
import { RegisterRequestDTO } from '../../core/models/auth/requests/register-request.dto';
import { AuthService } from '../../core/services/auth.service';
import { MessageResponseDTO } from '../../core/models/message-response.dto';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})

export class Register {
    isLoading: boolean = false;

    firstName: String = ''
    lastName: String = ''
    country: String = ''
    city: String = ''
    street: String = ''
    gender: String = ''
    email: String = ''
    phoneNumber: String = ''
    password: String = ''
    confirmPassword: String = ''

    profilePic: File | null = null

    error: String = ''
    success: String = ''

    constructor(private cdRef: ChangeDetectorRef, private authService: AuthService){}

    onFileChange(event: Event): void{
      const input = event.target as HTMLInputElement;
      this.profilePic = input.files?.[0] || null;
    }


    onSubmit(){
      this.isLoading = true; 
      this.cdRef.detectChanges();
      this.error = '';
      this.success = '';

      if(this.password !== this.confirmPassword){
        this.error = 'Passwords must match'
        this.isLoading = false;
        return
      }

      const error = this.authService.isPasswordValid(this.password.toString())
      if(error){
        this.error = error
        this.isLoading = false;
        return
      }

      const formData = new FormData()
      formData.append('firstName', String(this.firstName).trim());
      formData.append('lastName', String(this.lastName).trim());
      formData.append('email', String(this.email).trim());
      formData.append('phoneNumber', String(this.phoneNumber).trim());
      formData.append('address', `${this.street}, ${this.city}, ${this.country}`);
      formData.append('gender', this.gender.toString().toUpperCase());
      formData.append('password', String(this.password));
      formData.append('confirmPassword', String(this.confirmPassword))

      if(this.profilePic){
        formData.append('profilePhoto', this.profilePic)
      }

      this.authService.createRegisterRequest(formData).subscribe({
        next: (response: MessageResponseDTO) => {
          this.success = response.message;
          this.error = ''; 
          this.isLoading = false;
          this.cdRef.detectChanges()
        },
        error: (err: HttpErrorResponse) => {
          switch (err.status) {
            case 409:
              this.error = err.error?.message || 'User already exists';
              break;
            case 503:
              this.error = err.error?.message || 'Service temporarily unavailable';
              break;
            case 500:
              this.error = 'Internal server error';
              break;
            default:
              this.error = 'An unexpected error occurred';
          }
          this.isLoading = false;
          this.cdRef.detectChanges()
        }
      })
    }

    
}
