import { Injectable } from "@angular/core"

@Injectable({
  providedIn: 'root'  
})

export class RegisterService{
    isPasswordValid(password: string): String | null{
        if(password.length < 8) return 'Password must be over 8 characters long'
        if(!/[0-9]/.test(password)) return 'Password must contain a number';
        if(!/[a-z]/.test(password)) return 'Password must contain a lowercase letter'
        if (!/[A-Z]/.test(password)) return 'Password must contain an uppercase letter';

        return null
    }

    async fileToByteArray(file: File): Promise<number[]> {
        const arrayBuffer = await file.arrayBuffer()
        const uint8Array = new Uint8Array(arrayBuffer)

        return Array.from(uint8Array)
    }
}