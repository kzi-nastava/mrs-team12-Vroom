import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { MessageResponseDTO } from "../models/message-response.dto";
import { LoginResponseDTO } from "../models/auth/responses/login-response.dto";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class DriverService{
    private api = 'http://localhost:8080/api/driver'

    constructor(private http: HttpClient) {}


    createChangeStatusRequest(id: number, status: 'AVAILABLE' | 'UNAVAILABLE'): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(`${this.api}/${id}/status`, {status})
    }
}