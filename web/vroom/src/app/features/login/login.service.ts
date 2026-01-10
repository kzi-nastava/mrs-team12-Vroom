import { Injectable } from "@angular/core"
import { HttpClient } from '@angular/common/http';
import { Observable } from "rxjs";
import { LoginResponseDTO } from "../../core/models/auth/responses/login-response.dto";
import { MessageResponseDTO } from "../../core/models/message-response.dto";

@Injectable({
  providedIn: 'root'  
})
export class LoginService{
    private api = 'http://localhost:8080/api/auth'


    constructor(private http: HttpClient) {}

    createForgotPasswordRequest(data: any): Observable<MessageResponseDTO>{
        return this.http.post<MessageResponseDTO>(this.api+'/forgot-password', data);
    }

    createLoginRequest(data: any): Observable<LoginResponseDTO>{
        return this.http.post<LoginResponseDTO>(this.api+'/login', data);
    }
}