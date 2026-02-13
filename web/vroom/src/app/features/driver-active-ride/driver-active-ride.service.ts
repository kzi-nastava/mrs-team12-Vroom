import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ride } from './ride.model';

@Injectable({ providedIn: 'root' })
export class DriverActiveRideService {

  private baseUrl = 'http://localhost:8080/api/rides';

  constructor(private http: HttpClient) {}

getActiveRides(): Observable<Ride[]> { 
  const token = localStorage.getItem('jwt'); 
  console.log('Sending token for active rides:', token);
  return this.http.get<Ride[]>(`${this.baseUrl}/active`, {
    headers: { Authorization: `Bearer ${token}` }
  });
}

startRide(rideID : number): Observable<Ride> {
  const token = localStorage.getItem('jwtToken');
  return this.http.put<Ride>(`${this.baseUrl}/start/${rideID}`, {}, {
    headers: { Authorization: `Bearer ${token}` }
  });
}

}
