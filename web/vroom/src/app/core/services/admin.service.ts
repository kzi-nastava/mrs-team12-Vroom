import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserRideHistoryResponseDTO } from "../models/ride/responses/user-ride-history-respose.dto";

@Injectable({
    providedIn: 'root'
})
export class AdminService{
    private api = "http://localhost:8080/api/admins"

    constructor(private http: HttpClient){}

    getRideHistoryRequest(
        startDate?: any,
        endDate?: any,
        sortBy?: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc',
        userEmail?: string
    ): Observable<UserRideHistoryResponseDTO[]>{
        let params = new HttpParams().set('sort', sortBy || 'startTime,desc');
        if (startDate) {
            const start = new Date(startDate);
            params = params.set('startDate', start.toISOString());
        }
        if (endDate) {
            const end = new Date(endDate);
            params = params.set('endDate', end.toISOString());
        }

        if(userEmail){
            params = params.set('userEmail', userEmail)
        }

        return this.http.get<UserRideHistoryResponseDTO[]>(`${this.api}/users/rides`, { params })
    } 
}