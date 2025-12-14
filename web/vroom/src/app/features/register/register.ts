import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { RegisterService } from './register.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})

export class Register {
    firstName: String = ''
    lastName: String = ''
    country: String = ''
    city: String = ''
    street: String = ''
    gender: String = ''
    email: String = ''
    phoneNumber: String = ''
    password: String = ''
    rePassword: String = ''

    profilePic: File | null = null

    error: String = ''

    constructor(private registerService: RegisterService){}

    onFileChange(event: Event): void{
      const input = event.target as HTMLInputElement;

      if (!input.files || input.files.length === 0) {
        this.profilePic = null;
        return;
      }

      this.profilePic = input.files[0];
    }


    async onSubmit(): Promise<void>{
      if(this.password !== this.rePassword){
        this.error = 'Passwords must match'
        return
      }

      const error = this.registerService.isPasswordValid(this.password.toString())
      if(error){
        this.error = error
        return
      }
      
    }
}
