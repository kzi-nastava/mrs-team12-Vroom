import { Injectable } from "@angular/core"
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from "rxjs";
import { MessageResponseDTO } from "../../core/models/message-response.dto";

@Injectable({
  providedIn: 'root'  
})
export class RegisterDriverService{
    private apiUrl = 'http://localhost:8080/api/drivers';
    
    constructor(private http: HttpClient) {}

    validateDriverPreferences(
      numberOfSeats: number | null,
      petsAllowed: boolean | null,
      babiesAllowed: boolean | null
    ): string | null {
      if (numberOfSeats === null || numberOfSeats === undefined) {
        return 'Number of seats is required when vehicle information is provided';
      }

      const seats = Number(numberOfSeats);

      if (isNaN(seats)) {
        return 'Number of seats must be a number';
      }

      if (seats <= 0) {
        return 'Number of seats must be a positive number';
      }

      if (petsAllowed === null) {
        return 'You must select whether pets are allowed when vehicle information is provided';
      }

      if (babiesAllowed === null) {
        return 'You must select whether babies are allowed when vehicle information is provided';
      }

      return null; 
    }

    getFileValidationError(file: File): string | null {
        if (!file.type.startsWith('image/')) {
          return 'Please select a valid image file (png, jpg, etc.)';
        }

        if (file.size > 2 * 1024 * 1024) {
          return 'Image size must be less than 2MB';
        }

        return null;
    }

    async fileToBase64(file: File): Promise<string> {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();

            reader.readAsDataURL(file);
            reader.onload = () => {
                const base64String = (reader.result as string).split(',')[1];
                resolve(base64String);
            };

            reader.onerror = error => reject(error);
        });
    }

    createRequest(data: any): Observable<MessageResponseDTO> {
        const token = localStorage.getItem('token');
        
        const headers = new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        });

        return this.http.post<MessageResponseDTO>(
            this.apiUrl + '/register/driver', 
            data,
            { headers }
        );
    }
}