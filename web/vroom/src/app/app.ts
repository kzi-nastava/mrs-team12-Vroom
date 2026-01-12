import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './features/navbar/navbar';
import { NgToastComponent } from 'ng-angular-popup';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, NgToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  showNavbar = true;
  protected readonly title = signal('vroom');
}
