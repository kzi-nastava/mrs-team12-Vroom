import { Component } from '@angular/core';
import { Router } from '@angular/router';


@Component({
  selector: 'app-admin-home-page',
  imports: [],
  templateUrl: './admin-home-page.html',
  styleUrl: './admin-home-page.css',
  
})
export class AdminHomePage {
  
  constructor(
    private router : Router
  ){}
  
  manageUsers(){
    this.router.navigate(["/admin-users"]);
  }

  reports(){
    this.router.navigate(["/ride-statistics"]);
  }

  profileRequests(){
    this.router.navigate(["/admin-driver-requests"]);
  }

  addDriver(){
    this.router.navigate(["/register-driver"]);
  }

  rideHistory(){
    this.router.navigate(["/ride-history"]);
  }

  panicFeed(){
    this.router.navigate(["/panic-feed"]);
  }

  pricelist(){
    this.router.navigate(["/pricelist"]);
  }
}
