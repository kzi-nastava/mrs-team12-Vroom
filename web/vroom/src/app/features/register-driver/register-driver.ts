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
export class RegisterDriver {

  isLoading = false;

  firstName = '';
  lastName = '';
  country = '';
  city = '';
  street = '';
  gender = '';
  email = '';
  phoneNumber = '';

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

    const requiredError = this.validateRequiredFields();
    if (requiredError) {
      this.error = requiredError;
      this.isLoading = false;
      return;
    }

    const phoneError = this.validatePhoneNumber();
    if (phoneError) {
      this.error = phoneError;
      this.isLoading = false;
      return;
    }

    const hasAnyVehicleData = this.hasAnyVehicleData();
    const hasAllVehicleData = this.hasAllVehicleData();

    if (hasAnyVehicleData && !hasAllVehicleData) {
      this.error = 'If you enter vehicle information, all vehicle fields must be completed.';
      this.isLoading = false;
      return;
    }

    if (hasAllVehicleData) {
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
    }

    let photoBase64: string | undefined;
    if (this.profilePic) {
      try {
        photoBase64 = await this.registerService.fileToBase64(this.profilePic);
      } catch {
        this.error = 'Failed to process profile image.';
        this.isLoading = false;
        return;
      }
    }

    const data: any = {
      firstName: this.firstName.trim(),
      lastName: this.lastName.trim(),
      email: this.email.trim(),
      phoneNumber: this.phoneNumber.trim(),
      address: `${this.street}, ${this.city}, ${this.country}`,
      gender: this.gender,
      profilePhoto: photoBase64,
    };

    if (hasAllVehicleData) {
      data.brand = this.vehicleBrand.trim();
      data.model = this.vehicleModel.trim();
      data.type = this.vehicleType;
      data.licenceNumber = this.plateNumber.trim();
      data.numberOfSeats = this.numberOfSeats;
      data.petsAllowed = this.petsAllowed;
      data.babiesAllowed = this.babiesAllowed;
    }

    try {
      const response = await firstValueFrom(
        this.registerService.createRequest(data)
      );
      this.success = response.message || 'Driver registered! Activation mail sent.';
    
      this.resetForm();
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
        this.error = 'Unexpected error occurred.';
      }
    } finally {
      this.isLoading = false;
      this.cdRef.detectChanges();
    }
  }

  private hasAnyVehicleData(): boolean {
    return !!(
      this.vehicleBrand ||
      this.vehicleModel ||
      this.vehicleType ||
      this.plateNumber ||
      this.numberOfSeats !== null ||
      this.petsAllowed !== null ||
      this.babiesAllowed !== null
    );
  }

  private hasAllVehicleData(): boolean {
    return !!(
      this.vehicleBrand &&
      this.vehicleModel &&
      this.vehicleType &&
      this.plateNumber &&
      this.numberOfSeats !== null &&
      this.petsAllowed !== null &&
      this.babiesAllowed !== null
    );
  }

  private isEmpty(value: any): boolean {
    return value === null || value === undefined || value.toString().trim() === '';
  }

  private validateRequiredFields(): string | null {
    if (
      this.isEmpty(this.firstName) ||
      this.isEmpty(this.lastName) ||
      this.isEmpty(this.country) ||
      this.isEmpty(this.city) ||
      this.isEmpty(this.street) ||
      this.isEmpty(this.gender) ||
      this.isEmpty(this.email) ||
      this.isEmpty(this.phoneNumber)
    ) {
      return 'All personal information fields are required.';
    }

    return null;
  }

  private validatePhoneNumber(): string | null {
    const phoneRegex = /^[0-9]+$/;
    if (!phoneRegex.test(this.phoneNumber)) {
      return 'Phone number must contain only digits.';
    }
    return null;
  }

  private resetForm(): void {
    this.firstName = '';
    this.lastName = '';
    this.country = '';
    this.city = '';
    this.street = '';
    this.gender = '';
    this.email = '';
    this.phoneNumber = '';
    this.profilePic = null;
    this.vehicleBrand = '';
    this.vehicleModel = '';
    this.vehicleType = '';
    this.plateNumber = '';
    this.numberOfSeats = null;
    this.petsAllowed = null;
    this.babiesAllowed = null;
  }
}