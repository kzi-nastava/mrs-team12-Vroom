import { Component, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { RegisterDriverService } from './register-driver.service';
import { firstValueFrom } from 'rxjs';
import { isHttpError } from '../../core/utils/http-error.guard';

@Component({
  selector: 'app-register-driver',
  standalone: true,
  imports: [FormsModule, CommonModule, HttpClientModule],
  templateUrl: './register-driver.html',
  styleUrls: ['./register-driver.css'],
})
export class RegisterDriver{

  isLoading = false;


  firstName = '';
  lastName = '';
  country = '';
  city = '';
  street = '';
  gender = '';
  email = '';
  phoneNumber = '';
  password = '';
  rePassword = '';

  profilePic: File | null = null;


  vehicleBrand = '';
  vehicleModel = '';
  vehicleType: 'LUXURY' | 'STANDARD' | 'MINIVAN' | '' = '';
  plateNumber = '';
  numberOfSeats: number | null = null;

  petsAllowed: boolean | null = null;
  babiesAllowed: boolean | null = null;

  error = '';
  success = '';

  constructor(
    private registerService: RegisterDriverService,
    private cdRef: ChangeDetectorRef
  ) {}

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.profilePic = input.files?.[0] || null;
  }

  async onSubmit(): Promise<void> {
    this.isLoading = true;
    this.error = '';
    this.success = '';
    this.cdRef.detectChanges();


    if (this.password !== this.rePassword) {
      this.error = 'Passwords must match';
      this.isLoading = false;
      return;
    }

    const pwdError = this.registerService.isPasswordValid(this.password);
    if (pwdError) {
      this.error = pwdError;
      this.isLoading = false;
      return;
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

    let photoBase64: string | undefined;
    if (this.profilePic) {
      try {
        photoBase64 = await this.registerService.fileToBase64(this.profilePic);
      } catch {
        this.error = 'Failed to process image profile.';
        this.isLoading = false;
        return;
      }
    }

    const data = {
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim(),
      phoneNumber: this.phoneNumber.trim(),
      address: `${this.street}, ${this.city}, ${this.country}`,
      gender: this.gender ? this.gender.toUpperCase() : null,
      password: this.password,
      profilePhoto: photoBase64,


      brand: this.vehicleBrand.trim(),
      model: this.vehicleModel.trim(),
      type: this.vehicleType, 
      licenceNumber: this.plateNumber.trim(),
      numberOfSeats: this.numberOfSeats,
      petsAllowed: this.petsAllowed,
      babiesAllowed: this.babiesAllowed,
    };

    try {
      const response = await firstValueFrom(
        this.registerService.createRequest(data)
      );
      this.success = response.message;
    } catch (err) {
      if (isHttpError(err)) {
        this.error =
          err.error?.message ||
          (err.status === 409
            ? 'User already exists'
            : err.status === 503
            ? 'Service temporarily unavailable'
            : 'Internal server error');
      } else {
        this.error = 'An unexpected error occurred';
      }
    } finally {
      this.isLoading = false;
      this.cdRef.detectChanges();
    }
  }
}
