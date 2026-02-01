// driver-location.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DriverLocation {
  id: number;
  driver: { id: number };
  latitude: number;
  longitude: number;
  lastUpdated: string;
}

@Injectable({ providedIn: 'root' })
export class DriverLocationService {
  private API = 'http://localhost:8080/api/locations';

  constructor(private http: HttpClient) {}

  getAllLocations(): Observable<DriverLocation[]> {
    return this.http.get<DriverLocation[]>(this.API);
  }
}
