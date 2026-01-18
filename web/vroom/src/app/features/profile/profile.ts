import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService } from '../profile/profile.service'; 
import { Observable, tap } from 'rxjs';
import {Profile as ProfileModel} from '../profile/profile.model'; 

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'],
})
export class Profile implements OnInit {

  userRole: 'DRIVER' | 'PASSENGER' = 'PASSENGER';
  profile$!: Observable<ProfileModel>;   
  profileData!: ProfileModel;             
  editMode = false;

  constructor(private profileService: ProfileService) { }

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile() {
    this.profile$ = this.profileService.getMyProfile()
      .pipe(
        tap(data => this.profileData = { ...data }) 
      );
  }

  onChange() {
    if (this.editMode) {
      this.profileService.updateMyProfile(this.profileData)
        .subscribe({
          next: updated => {
            console.log('Profile saved', updated);
            this.profileData = { ...updated }; 
            this.loadProfile();
          },
          error: err => console.error('Error saving profile', err)
        });
    }
    this.editMode = !this.editMode;
  }
}