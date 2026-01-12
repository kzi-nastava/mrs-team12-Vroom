import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-order-from-favorites',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-from-favorites.html',
  styleUrl: './order-from-favorites.css'
})
export class OrderFromFavorites {

  favoriteRoutes = [
    {
      id: 1,
      name: 'Home → Faculty',
      start: 'Bulevar Patrijarha Pavla 37',
      end: 'FTN Novi Sad',
      price: 520
    },
    {
      id: 2,
      name: 'Work → Home',
      start: 'Ilirska 23',
      end: 'Stražilovska 2',
      price: 480
    }
  ];

  useRoute(route: any) {
    console.log('Using route:', route);
    // this.rideForm.start = route.start;
    // this.rideForm.end = route.end;
  }

  removeFromFavorites(route: any) {
    this.favoriteRoutes =
      this.favoriteRoutes.filter(r => r.id !== route.id);
  }
}
