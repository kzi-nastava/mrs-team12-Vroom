import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Profile as ProfileModel } from '../profile/profile.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private apiUser = 'http://localhost:8080/api/profile/user/me';
  private apiDriver = 'http://localhost:8080/api/profile/driver/me';

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<ProfileModel> {
    const token = localStorage.getItem('jwt');
    return this.http.get<ProfileModel>(this.apiUser, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

  updateMyProfile(profile: ProfileModel): Observable<ProfileModel> {
    const token = localStorage.getItem('jwt');
    return this.http.put<ProfileModel>(this.apiUser, profile, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }
}
