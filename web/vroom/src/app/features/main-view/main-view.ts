// main-view.component.ts
import { Component, AfterViewInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import * as L from 'leaflet';
import { filter, Subject, takeUntil } from 'rxjs';
import { MapService } from '../../core/services/map.service';
import { MapActionType } from '../../core/models/map/enums/map-action-type.enum';
import { HttpClient } from '@angular/common/http';
import { DriverLocationService } from '../driver-location/driver-location.service';

@Component({
  selector: 'app-map',
  imports: [RouterOutlet],
  templateUrl: './main-view.html',
  styleUrl: './main-view.css',
})
export class MainView implements AfterViewInit {
  private map!: L.Map;
  private centroid: L.LatLngExpression = [45.2455, 19.8227];
  private routeLayer: L.LayerGroup = L.layerGroup();
  private destroy$ = new Subject<void>();
  private vehiclesLayer: L.LayerGroup=L.layerGroup();
  
  private routesWithMap = [
    '/route-estimation',
    '/order-a-ride',
    '/ride-duration',
    '/ride-review'
  ];
  

  constructor(
    private mapService: MapService,
    private http: HttpClient,
    private router: Router,
    private driverLocationService: DriverLocationService
  ) {}

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


    // setup route listener to clear things from the map when changing route
    this.setupRouteListener()
    
    // setup map service listener when action happens there to update the map
    this.setupMapServiceListener()
  }

  private setupMapServiceListener(): void{
    this.mapService.mapAction$
      .pipe(takeUntil(this.destroy$))
      .subscribe(action => {
        switch (action.type) {
          case MapActionType.DRAW_ROUTE:
            this.handleDrawRoute(action.payload);
            break;
          case MapActionType.CLEAR_MAP:
            this.routeLayer.clearLayers();
            break;
          case MapActionType.SHOW_VEHICLES:
            this.showVehiclesOnMap();
            break;
        }
      });
  }

  private setupRouteListener(): void{
    this.routeLayer.addTo(this.map);
    this.vehiclesLayer.addTo(this.map);

    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe((event: any) => {
        const currentUrl = event.urlAfterRedirects;
        if (!this.routesWithMap.some(route => currentUrl.includes(route))) {
          this.routeLayer.clearLayers();
          this.map.setView(this.centroid, 14); 
        }
      });
  }

  private async handleDrawRoute(payload: any): Promise<void> {
    // clear all older layers
    this.routeLayer.clearLayers();

    try {
      // get coordinates for every straight line
      const route = await this.mapService.getRouteCoordinates(payload);

      if(!route) throw new Error

      // map coordinates to leaflet
      const routeCoordinates: L.LatLngTuple[] = route.geometry.coordinates.map(
        (coord: number[]) => [coord[1], coord[0]] as L.LatLngTuple 
      );
      
      // draw lines from each coordinate to next one
      L.polyline(routeCoordinates, { 
        color: '#2A2C24', 
        weight: 5,
        opacity: 0.7 
      }).addTo(this.routeLayer);
      
      // add markers
      this.addRouteMarkers(payload)

      this.map.fitBounds(L.latLngBounds(routeCoordinates), { padding: [50, 50] });
    } catch (error) {
      this.drawStraightLine(payload);
    }
  }

  private addRouteMarkers(payload: any): void{
    if (payload.start) {
        L.marker([payload.start.lat, payload.start.lng], {
          icon: this.createCustomIcon('🚗')
        }).addTo(this.routeLayer).bindPopup('Start');
      }
      
      payload.stops?.forEach((stop: any, i: number) => {
        L.marker([stop.lat, stop.lng], {
          icon: this.createCustomIcon((i + 1).toString())
        }).addTo(this.routeLayer).bindPopup(`Stop ${i + 1}`);
      });
      
      if (payload.end) {
        L.marker([payload.end.lat, payload.end.lng], {
          icon: this.createCustomIcon('🏁')
        }).addTo(this.routeLayer).bindPopup('End');
      }
  }

  private drawStraightLine(payload: any): void {
    const points: L.LatLngTuple[] = [];

    if (payload.start) {
      const start: L.LatLngTuple = [payload.start.lat, payload.start.lng];
      L.marker(start).addTo(this.routeLayer).bindPopup('Start');
      points.push(start);
    }

    payload.stops?.forEach((stop: any, i: number) => {
      const pos: L.LatLngTuple = [stop.lat, stop.lng];
      L.marker(pos).addTo(this.routeLayer).bindPopup(`Stop ${i + 1}`);
      points.push(pos);
    });

    if (payload.end) {
      const end: L.LatLngTuple = [payload.end.lat, payload.end.lng];
      L.marker(end).addTo(this.routeLayer).bindPopup('End');
      points.push(end);
    }

    if (points.length >= 2) {
      L.polyline(points, { color: '#2A2C24', weight: 5 }).addTo(this.routeLayer);
      this.map.fitBounds(L.latLngBounds(points), { padding: [50, 50] });
    }
  }

  private createCustomIcon(label: string): L.DivIcon {
    return L.divIcon({
      html: `<div style="background-color: #2A2C24; color: white; border-radius: 50%; width: 30px; height: 30px; display: flex; align-items: center; justify-content: center; font-weight: bold; border: 2px solid white;">${label}</div>`,
      className: '',
      iconSize: [30, 30],
      iconAnchor: [15, 15]
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.mapService.clearMap();
  }

 private showVehiclesOnMap(): void {
  this.vehiclesLayer.clearLayers();

  this.driverLocationService.getAllLocations()
    .subscribe(locations => {

      locations.forEach(loc => {

        const marker = L.marker([loc.latitude, loc.longitude], {
          icon: this.createCustomIcon('🚕')
        });

        marker.bindPopup(
          `Driver #${loc.driver.id}<br/>
           Last update: ${new Date(loc.lastUpdated).toLocaleTimeString()}`
        );

        marker.addTo(this.vehiclesLayer);
      });

    });
}

}