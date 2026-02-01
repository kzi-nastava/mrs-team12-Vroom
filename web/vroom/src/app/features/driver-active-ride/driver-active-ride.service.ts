import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ride } from './ride.model';

@Injectable({ providedIn: 'root' })
export class DriverActiveRideService {

  private baseUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

  getActiveRide(): Observable<Ride | null> {
    const token = localStorage.getItem('jwt'); 
    console.log('Sending token for active ride:', token);
    return this.http.get<Ride | null>(`${this.baseUrl}/active`, {
      headers: { Authorization: `Bearer ${token}` }
    });
  }

startRide(rideID : number): Observable<Ride> {
  const token = localStorage.getItem('jwtToken');
  return this.http.put<Ride>(`${this.baseUrl}/start/${rideID}`, {}, {
    headers: { Authorization: `Bearer ${token}` }
  });
}


  finishRide(): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/finish`, {});
  }
}
