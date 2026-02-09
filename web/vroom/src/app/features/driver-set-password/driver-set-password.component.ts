import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-driver-set-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './driver-set-password.component.html',
  styleUrls: ['./driver-set-password.component.css']
})
export class DriverSetPasswordComponent implements OnInit {
  driverId: string = '';
  password: string = '';
  confirmPassword: string = '';
  
  message: string = '';
  messageType: 'success' | 'error' | '' = '';
  isLoading: boolean = false;

  private apiUrl = 'http://localhost:8080/api';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit(): void {

    this.driverId = this.route.snapshot.paramMap.get('driverId') || '';
    
    if (!this.driverId) {
      this.showMessage('Neispravan aktivacioni link', 'error');
    }
  }

  onSubmit(): void {

    this.message = '';
    this.messageType = '';


    if (!this.password || !this.confirmPassword) {
      this.showMessage('Molimo popunite sva polja', 'error');
      return;
    }

    if (this.password.length < 8) {
      this.showMessage('Lozinka mora imati najmanje 8 karaktera', 'error');
      return;
    }

    if (this.password !== this.confirmPassword) {
      this.showMessage('Lozinke se ne poklapaju', 'error');
      return;
    }

 
    if (!this.isPasswordValid(this.password)) {
      this.showMessage(
        'Lozinka mora sadržati: veliko slovo, malo slovo, broj i specijalni karakter',
        'error'
      );
      return;
    }

    this.setPassword();
  }

  private isPasswordValid(password: string): boolean {
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
  
    
    return hasUpperCase && hasLowerCase && hasNumber;
  }

  private setPassword(): void {
    this.isLoading = true;

    const requestBody = {
      password: this.password,
      confirmPassword: this.confirmPassword
    };

this.http.post(
  `${this.apiUrl}/drivers/driver/set-password/${this.driverId}`, 
  requestBody
).subscribe({
      next: (response: any) => {
        this.showMessage('Lozinka uspešno postavljena! Prebacujemo vas na stranicu za prijavu...', 'success');
        
  
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      },
      error: (error) => {
        this.isLoading = false;
        
        let errorMessage = 'Došlo je do greške. Pokušajte ponovo.';
        
        if (error.error && error.error.error) {
          errorMessage = error.error.error;
        } else if (error.status === 400) {
          errorMessage = 'Aktivacioni link je istekao ili je već iskorišćen';
        }
        
        this.showMessage(errorMessage, 'error');
      }
    });
  }

  private showMessage(text: string, type: 'success' | 'error'): void {
    this.message = text;
    this.messageType = type;

    if (type === 'error') {
      setTimeout(() => {
        this.message = '';
        this.messageType = '';
      }, 5000);
    }
  }

  getPasswordStrength(): string {
    if (!this.password) return '';
    
    const length = this.password.length;
    const hasUpperCase = /[A-Z]/.test(this.password);
    const hasLowerCase = /[a-z]/.test(this.password);
    const hasNumber = /[0-9]/.test(this.password);
  
    
    const strength = [hasUpperCase, hasLowerCase, hasNumber]
      .filter(Boolean).length;
    
    if (length < 8) return 'weak';
    if (strength < 3) return 'weak';
    if (strength === 3) return 'medium';
    return 'strong';
  }
}
