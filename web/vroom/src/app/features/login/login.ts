import { Component, Input } from '@angular/core';
import {FormsModule} from '@angular/forms'
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {
  email: String = ''
  password: String = ''
  error: String = ''


  constructor(private router: Router){}

  onForgotPassword(): void{
    console.log(this.email)
    this.router.navigate(['/forgot-password'])
  }

  async onLogin(): Promise<void>{
    console.log(this.email, this.password)
  }
}
