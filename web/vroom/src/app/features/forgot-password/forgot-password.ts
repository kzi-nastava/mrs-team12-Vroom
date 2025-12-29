import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms'

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  email: String =''
  code: String =''
  password: String=''
  rePassword: String =''
  error: String=''

  async onSubmit(): Promise<void>{
    console.log(this.email, this.code, this.password, this.rePassword)
  }
}
