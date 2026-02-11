import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../profile/profile.service'; 
import { Observable, tap } from 'rxjs';
import { Profile as ProfileModel } from '../profile/profile.model'; 

interface PasswordChangeData {
  oldPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
})
export class Profile implements OnInit {

  userRole: 'DRIVER' | 'REGISTERED_USER' = 'DRIVER';
  profile$!: Observable<ProfileModel>;   
  profileData!: ProfileModel;             
  editMode = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  // Password change modal
  showPasswordModal = false;
  passwordData: PasswordChangeData = {
    oldPassword: '',
    newPassword: '',
    confirmNewPassword: ''
  };
  passwordSuccessMessage: string | null = null;
  passwordErrorMessage: string | null = null;

  constructor(private profileService: ProfileService) { }

  ngOnInit(): void {
    const storedType = localStorage.getItem('user_type');

    this.userRole = storedType === 'DRIVER'
      ? 'DRIVER'
      : 'REGISTERED_USER';

    this.loadProfile();
  }

  loadProfile() {
    this.profile$ = this.profileService.getMyProfile()
      .pipe(
        tap(data => this.profileData = { ...data }) 
      );
  }

  onChange() {
    this.successMessage = null;
    this.errorMessage = null;

    if (this.editMode) {
      this.profileService.updateMyProfile(this.profileData)
        .subscribe({
          next: updatedProfile => {
            this.profileData = { ...updatedProfile }; 
            this.editMode = false;
            this.successMessage = 'Your update request has been sent and is awaiting admin approval.';

            setTimeout(() => this.successMessage = null, 3000);
          },
          error: err => {
            this.errorMessage = err?.error?.message || 'Failed to submit update request.';
            setTimeout(() => this.errorMessage = null, 5000);
          }
        });
      return;
    }

    this.editMode = true;
  }

  togglePasswordModal() {
    this.showPasswordModal = !this.showPasswordModal;
    
    if (!this.showPasswordModal) {
      // Reset form when closing
      this.passwordData = {
        oldPassword: '',
        newPassword: '',
        confirmNewPassword: ''
      };
      this.passwordSuccessMessage = null;
      this.passwordErrorMessage = null;
    }
  }

  onChangePassword() {
    this.passwordSuccessMessage = null;
    this.passwordErrorMessage = null;

    this.profileService.changePassword(this.passwordData)
      .subscribe({
        next: (response) => {
          this.passwordSuccessMessage = 'Password changed successfully!';
          
          setTimeout(() => {
            this.togglePasswordModal();
          }, 2000);
        },
        error: (err) => {
          this.passwordErrorMessage = err?.error?.message || 'Failed to change password.';
        }
      });
  }
}