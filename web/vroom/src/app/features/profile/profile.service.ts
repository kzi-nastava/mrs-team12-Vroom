import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Profile } from '../profile/profile.model';

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private apiUser = 'http://localhost:8080/api/profile/user/me';
  private apiDriver = 'http://localhost:8080/api/profile/driver/me';

  constructor(private http: HttpClient) {}

  getMyProfile(isDriver: boolean = false) {
    const url = isDriver ? this.apiDriver : this.apiUser;
    return this.http.get<any>(url, {
      withCredentials: true
    });
  }

  updateMyProfile(payload: any, isDriver: boolean = false) {
    const url = isDriver ? this.apiDriver : this.apiUser;
    return this.http.put<any>(url, payload, {
      withCredentials: true
    });
  }
}
