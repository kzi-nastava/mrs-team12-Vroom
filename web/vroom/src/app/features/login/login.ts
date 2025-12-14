import { Component, Input } from '@angular/core';
import {FormsModule} from '@angular/forms'

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

  onForgotPassword(): void{
    console.log(this.email)
  }

  async onLogin(): Promise<void>{
    console.log(this.email, this.password)
  }
}
