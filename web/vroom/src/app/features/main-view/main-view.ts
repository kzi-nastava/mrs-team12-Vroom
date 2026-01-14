import { Component, AfterViewInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  imports: [RouterOutlet],
  templateUrl: './main-view.html',
  styleUrl: './main-view.css',
})
export class MainView implements AfterViewInit {
  private map!: L.Map;
  private centroid: L.LatLngExpression = [45.2455, 19.8227];
  
  constructor() {}

  ngAfterViewInit(): void {
    this.map = L.map('map', {
      center: this.centroid,
      zoom: 14,
      scrollWheelZoom: false,
      dragging: true,
      touchZoom: true,
      doubleClickZoom: true
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);
  }
}
