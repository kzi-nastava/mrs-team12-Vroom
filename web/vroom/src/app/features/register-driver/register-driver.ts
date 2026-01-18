import { Component, ChangeDetectorRef  } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { RegisterDriverService } from './register-driver.service';
import { firstValueFrom } from 'rxjs';
import { isHttpError } from '../../core/utils/http-error.guard';
import { RegisterRequestDTO } from '../../core/models/auth/requests/register-request.dto';

@Component({
  selector: 'app-register-driver',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './register-driver.html',
  styleUrl: './register-driver.css',
})

export class RegisterDriver {
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
    rePassword: String = ''

    profilePic: File | null = null

    vehicleModel: string = '';
    vehicleType: string = '';
    plateNumber: string = '';
    numberOfSeats: number | null = null;

    petsAllowed: boolean | null = null;
    babiesAllowed: boolean | null = null;

    error: String = ''
    success: String = ''

    constructor(private registerService: RegisterDriverService, private cdRef: ChangeDetectorRef){}

    onFileChange(event: Event): void{
      const input = event.target as HTMLInputElement;
      this.profilePic = input.files?.[0] || null;
    }


    async onSubmit(): Promise<void>{
      this.isLoading = true; 
      this.cdRef.detectChanges();
      this.error = '';
      this.success = '';

      if(this.password !== this.rePassword){
        this.error = 'Passwords must match'
        this.isLoading = false;
        return
      }

      const error = this.registerService.isPasswordValid(this.password.toString())
      if(error){
        this.error = error
        this.isLoading = false;
        return
      }


  const prefError = this.registerService.validateDriverPreferences(
    this.numberOfSeats,
    this.petsAllowed,
    this.babiesAllowed
  );

  if (prefError) {
    this.error = prefError;
    this.isLoading = false;
    return;
  }
  

      let photoBase64: string | undefined = undefined;
      if (this.profilePic) {
        try {
          photoBase64 = await this.registerService.fileToBase64(this.profilePic);
        } catch (e) {
          this.error = "Failed to process image profile.";
          this.isLoading = false;
          return;
        }
      }

      const data: RegisterRequestDTO = {
        firstName: String(this.firstName).trim(),
        lastName: String(this.lastName).trim(),
        email: String(this.email).trim(),
        phoneNumber: String(this.phoneNumber).trim(),
        address: `${this.street}, ${this.city}, ${this.country}`,
        gender: this.gender.toString().toUpperCase() as 'MALE' | 'FEMALE' | 'OTHER',
        password: String(this.password),
        profilePhoto: photoBase64,
        type: "driver"
      };

      try{
        const response = await firstValueFrom(this.registerService.createRequest(data))
        this.success = response.message;
        this.error = ''; 
      }catch(err){
        if (isHttpError(err)) {
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
        } else {
          this.error = 'An unexpected error occurred';
        }
      } finally {
        this.isLoading = false; 
      }

      this.cdRef.detectChanges()
    }

}
