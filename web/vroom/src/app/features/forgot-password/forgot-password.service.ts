import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { MessageResponseDTO } from "../../core/models/message-response.dto";

@Injectable({
    providedIn:'root'
})
export class ForgotPasswordService{
    private apiUrl = 'http://localhost:8080/api/auth'

    constructor(private http: HttpClient) {}

    isPasswordValid(password: string): String | null{
        if(password.length < 8) return 'Password must be over 8 characters long'
        if(!/[0-9]/.test(password)) return 'Password must contain a number';
        if(!/[a-z]/.test(password)) return 'Password must contain a lowercase letter'
        if (!/[A-Z]/.test(password)) return 'Password must contain an uppercase letter';

        return null
    }

    createRequest(data: any): Observable<MessageResponseDTO>{
        return this.http.put<MessageResponseDTO>(this.apiUrl+'/reset-password', data)
    }
}