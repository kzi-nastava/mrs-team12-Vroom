import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminUser } from './admin-user.model';

@Injectable({
  providedIn: 'root'
})
export class AdminUserService {

  private apiUrl = 'http://localhost:8080/api/admins/users';

  constructor(private http: HttpClient) {}

  getAllUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(this.apiUrl);
  }

  blockUser(userId: number, reason: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${userId}/block`, {
      reason
    });
  }

  unblockUser(userId: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${userId}/unblock`, {});
  }
}