import { Injectable } from "@angular/core"
import { HttpClient } from '@angular/common/http';
import { Observable } from "rxjs";
import { MessageResponseDTO } from "../../core/models/message-response.dto";

@Injectable({
  providedIn: 'root'  
})
export class RegisterDriverService{
    private apiUrl = 'http://localhost:8080/api/drivers';
    
    constructor(private http: HttpClient) {}

    isPasswordValid(password: string): string | null{
        if(password.length < 8) return 'Password must be over 8 characters long'
        if(!/[0-9]/.test(password)) return 'Password must contain a number';
        if(!/[a-z]/.test(password)) return 'Password must contain a lowercase letter'
        if (!/[A-Z]/.test(password)) return 'Password must contain an uppercase letter';

        return null
        }

        validateDriverPreferences(
  numberOfSeats: number| null,
  petsAllowed: boolean | null,
  babiesAllowed: boolean | null
): string | null {

  if (numberOfSeats === null || numberOfSeats === undefined) {
    return 'Number of seats is required';
  }

  const seats = Number(numberOfSeats);

  if (isNaN(seats)) {
    return 'Number of seats must be a number';
  }

  if (seats <= 0) {
    return 'Number of seats must be a positive number';
  }

 
  if (petsAllowed === null) {
    return 'You must select whether pets are allowed';
  }


  if (babiesAllowed === null) {
    return 'You must select whether babies are allowed';
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
        return this.http.post<MessageResponseDTO>(this.apiUrl+'/register/driver', data)
    }
}