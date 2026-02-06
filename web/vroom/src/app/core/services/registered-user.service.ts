import { HttpClient, HttpParams } from "@angular/common/http";
import { Inject, Injectable } from "@angular/core";
import { RideResponseDTO } from "../models/ride/responses/ride-respose.dto";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class RegisteredUserService{
    private api = "http://localhost:8080/api/registered-user"

    constructor(private http: HttpClient){}

    getRideHistoryRequest(
        pageNum: number,
        pageSize: number,
        startDate?: any,
        endDate?: any,
        sortBy?: 'startTime,asc' | 'startTime,desc' | 'price,asc' | 'price,desc'
    ): Observable<RideResponseDTO[]>{
        let params = new HttpParams()
            .set('sort', sortBy || 'startTime,desc')
            .set('pageNumber', pageNum.toString())
            .set('pageSize', pageSize.toString())
            
        if (startDate) {
            const start = new Date(startDate);
            params = params.set('startDate', start.toISOString());
        }
        if (endDate) {
            const end = new Date(endDate);
            params = params.set('endDate', end.toISOString());
        }
        return this.http.get<RideResponseDTO[]>(`${this.api}/rides`, { params })
    } 
}