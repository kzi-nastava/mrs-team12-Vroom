import { HttpClient, HttpParams } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { UserRideHistoryResponseDTO } from "../models/ride/responses/user-ride-history-respose.dto";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class RegisteredUserService{
    private api = "http://localhost:8080/api/registered-user"

    constructor(private http: HttpClient){}

    getRideHistoryRequest(
        startDate?: any,
        endDate?: any,
        sortBy?: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc'
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
        return this.http.get<UserRideHistoryResponseDTO[]>(`${this.api}/rides`, { params })
    } 
}