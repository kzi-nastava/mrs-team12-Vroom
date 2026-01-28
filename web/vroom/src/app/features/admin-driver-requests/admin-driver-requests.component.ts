import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, Observable, switchMap, catchError, of, tap } from 'rxjs';

interface DriverUpdateRequest {
  id: number;
  driverId: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createdAt: string;
  payload: any;
}

@Component({
  selector: 'app-admin-driver-requests',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './admin-driver-requests.html',
  styleUrls: ['./admin-driver-requests.css']
})
export class AdminDriverRequestsComponent {

  private reload$ = new BehaviorSubject<void>(undefined);

  requests$: Observable<DriverUpdateRequest[]> = this.reload$.pipe(
    switchMap(() =>
      this.http.get<DriverUpdateRequest[]>(
        'http://localhost:8080/api/admins/driver-update-requests'     
      ).pipe(
        catchError(() => {
          this.error = 'Failed to load requests.';
          return of([]);
        })
      )
    )
  );

  success = '';
  error = '';
  rejectComment: Record<number, string> = {};

  constructor(private http: HttpClient) {}

  private refresh(): void {
    this.reload$.next();
  }

  approve(id: number): void {
    if (!confirm('Approve this profile update?')) return;

    this.http.post(
      `http://localhost:8080/api/admins/driver-update-requests/${id}/approve`,
      {}
    ).subscribe({
      next: () => {
        this.success = 'Profile update approved successfully.';
        this.error = '';
        this.refresh();
      },
      error: () => {
        this.error = 'Failed to approve request.';
        this.success = '';
      }
    });
  }

  reject(id: number): void {
    const comment = this.rejectComment[id];

    if (!comment || comment.trim() === '') {
      this.error = 'Reject comment is required.';
      return;
    }

    this.http.post(
      `http://localhost:8080/api/admins/driver-update-requests/${id}/reject`,
      { comment }
    ).subscribe({
      next: () => {
        this.success = 'Profile update rejected successfully.';
        this.error = '';
        delete this.rejectComment[id];
        this.refresh();
      },
      error: () => {
        this.error = 'Failed to reject request.';
        this.success = '';
      }
    });
  }
}

