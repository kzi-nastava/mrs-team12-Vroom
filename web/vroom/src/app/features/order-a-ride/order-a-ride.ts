import { Component, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

@Component({
  selector: 'app-order-a-ride',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-a-ride.html',
  styleUrls: ['./order-a-ride.css']
})
export class OrderARide implements AfterViewInit {

  private map!: L.Map;

  ngAfterViewInit(): void {
    this.initMap();
  }

private initMap(): void {
  setTimeout(() => { 
    const mapEl = document.getElementById('map');
    if (!mapEl) return;

  
    mapEl.style.height = mapEl.offsetHeight + 'px';

    this.map = L.map(mapEl!).setView([45.2671, 19.8335], 14);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap'
    }).addTo(this.map);

    this.map.invalidateSize();
  }, 0);

  }
}
