import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideReportDTO } from './ride-report.model';

@Injectable({ providedIn: 'root' })
export class RideStatisticsService {

  constructor(private http: HttpClient) { }

private baseUrl = 'http://localhost:8080/api/reports';

getPassengerReport(userId: number, from: string, to: string): Observable<RideReportDTO> {
  return this.http.get<RideReportDTO>(
    `${this.baseUrl}/me?from=${from}&to=${to}`
  );
}

getDriverReport(driverId: number, from: string, to: string): Observable<RideReportDTO> {
  return this.http.get<RideReportDTO>(
    `${this.baseUrl}/driver/me?from=${from}&to=${to}`
  );
}

getAdminUserReport(userId: number, from: string, to: string): Observable<RideReportDTO> {
  const token = localStorage.getItem('jwt'); 
  return this.http.get<RideReportDTO>(
    `${this.baseUrl}/admin/user/${userId}?from=${from}&to=${to}`,
    {
      headers: { Authorization: `Bearer ${token}` }
    }
  );
}

getAdminDriverReport(driverId: number, from: string, to: string): Observable<RideReportDTO> {
  const token = localStorage.getItem('jwt');
  return this.http.get<RideReportDTO>(
    `${this.baseUrl}/admin/driver/${driverId}?from=${from}&to=${to}`,
    {
      headers: { Authorization: `Bearer ${token}` }
    }
  );
}

getAdminAllUsersReport(from: string, to: string): Observable<RideReportDTO> {
  return this.http.get<RideReportDTO>(
    `${this.baseUrl}/admin/users?from=${from}&to=${to}`
  );
}

getAdminAllDriversReport(from: string, to: string): Observable<RideReportDTO> {
  return this.http.get<RideReportDTO>(
    `${this.baseUrl}/admin/drivers?from=${from}&to=${to}`
  );
}


}
