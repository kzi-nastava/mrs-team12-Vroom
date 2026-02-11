import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Profile as ProfileModel } from '../profile/profile.model';
import { Observable } from 'rxjs';

interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
  confirmNewPassword: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private apiUser = 'http://localhost:8080/api/profile/user/me';
  private apiDriver = 'http://localhost:8080/api/profile/driver/me';
  
  private apiUserPassword = 'http://localhost:8080/api/profile/user/change-password';
  private apiDriverPassword = 'http://localhost:8080/api/profile/driver/change-password';

  constructor(private http: HttpClient) {}

  private getUrl(): string {
    const type = localStorage.getItem('user_type');

    return type === 'DRIVER'
      ? this.apiDriver
      : this.apiUser;
  }

  private getPasswordUrl(): string {
    const type = localStorage.getItem('user_type');

    return type === 'DRIVER'
      ? this.apiDriverPassword
      : this.apiUserPassword;
  }

  getMyProfile(): Observable<ProfileModel> {
    const token = localStorage.getItem('jwt');

    return this.http.get<ProfileModel>(this.getUrl(), {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  updateMyProfile(profile: ProfileModel): Observable<ProfileModel> {
    const token = localStorage.getItem('jwt');

    return this.http.put<ProfileModel>(this.getUrl(), profile, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  changePassword(data: ChangePasswordRequest): Observable<string> {
    const token = localStorage.getItem('jwt');

    return this.http.put<string>(this.getPasswordUrl(), data, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}