// main-view.component.ts
import { Component, AfterViewInit } from '@angular/core';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import * as L from 'leaflet';
import { catchError, filter, Observable, of, Subject, take, takeUntil } from 'rxjs';
import { MapService } from '../../core/services/map.service';
import { MapActionType } from '../../core/models/map/enums/map-action-type.enum';
import { DriverService } from '../../core/services/driver.service';
import { LocationUpdate } from '../../core/models/driver/location-update-response.dto';
import { RideUpdatesService } from '../../core/services/ride-update-service';
import { RideService } from '../../core/services/ride.service';
import { MapAction } from '../../core/models/map/interfaces/map-action.interface';

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
  private driverMarkers: Map<number, L.Marker> = new Map();
  
  private routesWithMap = [
    '',
    '/',
    '/route-estimation',
    '/order-a-ride',
    '/ride-duration',
    '/ride-review'
  ];
  
  constructor(
    private mapService: MapService,
    private router: Router,
    private driverService: DriverService,
    private rideUpdatesService: RideUpdatesService,
    private rideService: RideService
  ) {}

  ngAfterViewInit(): void {
    this.map = L.map('map', {
      center: this.centroid,
      zoom: 16,
      scrollWheelZoom: false,
      dragging: true,
      touchZoom: true,
      doubleClickZoom: true
    });
    this.centerOnUser();

    this.map.createPane('routePane');
    this.map.createPane('vehiclePane');
    (this.map.getPane('routePane') as HTMLElement).style.zIndex = '399';
    (this.map.getPane('vehiclePane') as HTMLElement).style.zIndex = '650';

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.routeLayer.addTo(this.map);
    this.vehiclesLayer.addTo(this.map);

    // setup route listener to clear things from the map when changing route
    this.setupRouteListener();
    
    // setup map service listener when action happens there to update the map
    this.setupMapServiceListener();

    this.setupRealTimeLocationListener();
  }

  private centerOnUser(): void {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const userLocation: L.LatLngExpression = [
            position.coords.latitude,
            position.coords.longitude
          ];
          this.centroid = userLocation;
          this.map.setView(userLocation, 16);
        },
        () => console.warn('Location access denied. Using default centroid.')
      );
    }
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
        if (currentUrl === '/'  || currentUrl === '') {
          this.resetMap();
          this.setupRealTimeLocationListener();
          this.map.setView(this.centroid, 16); 
        }else if (this.routesWithMap.some(route => currentUrl.includes(route))) {
          this.resetMap();
          this.map.setView(this.centroid, 14); 
        }
      });
  } 

  private setupMapServiceListener(): void{
    this.mapService.mapAction$
      .pipe(
        takeUntil(this.destroy$),
        filter((action): action is MapAction => action !== null)
      )
      .subscribe(action => {
        switch (action.type) {
          case MapActionType.DRAW_ROUTE:
            this.driverService.disconnectWebSocket()
            this.resetMap();
            this.handleDrawRoute(action.payload);
            
            //this.routeLayer.clearLayers();
            break;
          case MapActionType.CLEAR_MAP:
            //this.routeLayer.clearLayers();
            this.resetMap()
            break;
          case MapActionType.SHOW_VEHICLES:
            this.driverService.disconnectWebSocket()
            //this.routeLayer.clearLayers();
            this.resetMap();
            this.setupRealTimeLocationListener();
            
            break;
          case MapActionType.RIDE_DURATION:
            this.driverService.disconnectWebSocket()
            //this.routeLayer.clearLayers();
            this.resetMap();
            this.setUpRideTracking(action.payload.rideID, "In Ride");
            
            break;
          case MapActionType.PANIC_RIDE:
            this.driverService.disconnectWebSocket()
            this.resetMap();
            this.setUpRideTracking(action.payload.rideID, "PANIC")

            break;
        }
      });
  }

  private resetMap(){
    this.routeLayer.clearLayers()
    this.vehiclesLayer.clearLayers();
    this.driverMarkers.clear()
  }

  private setUpRideTracking(rideID: string, type: string): void {
    this.routeLayer.clearLayers();

    this.rideUpdatesService.getRideUpdates().pipe(
      takeUntil(this.destroy$)
    ).subscribe(update => {
      this.updateSingleVehicleOnMap( -1,
        update.currentLocation.lat,
        update.currentLocation.lng,
        type
      );
    });
    this.rideService.getRouteDetails(rideID).subscribe({
      next: (ride) => {
        const payload = {
          start: { lat: ride.startLocationLat, lng: ride.startLocationLng },
          end: { lat: ride.endLocationLat, lng: ride.endLocationLng },
          stops: ride.stops
        };
        this.handleDrawRoute(payload);
      }
    });
  }


  private setupRealTimeLocationListener(): void {
    this.driverService.locationUpdates$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
          next: () => {
              this.driverService.locationUpdates$
                  .pipe(takeUntil(this.destroy$))
                  .subscribe((location: LocationUpdate) => {
                      this.updateSingleVehicleOnMap(
                          location.driverId,
                          location.point.lat,
                          location.point.lng,
                          location.status
                      );
                  });
          },
          error: (err) => {
              console.error('WebSocket connection failed', err);
          }
      });
    this.driverService.initializeWebSocket().subscribe();
  }



  public updateSingleVehicleOnMap(driverId: number, latitude: number, longitude: number, status: String): void {
    const existingMarker = this.driverMarkers.get(driverId);

    if (existingMarker) {
      existingMarker.setLatLng([latitude, longitude]);
    } else {
      const marker = L.marker([latitude, longitude], {
        // later add different colored icons based on status
        icon: this.showCarIcon(),
        pane: 'vehiclePane'
      }).addTo(this.vehiclesLayer);

      marker.bindPopup(
        `Driver #${driverId}<br/>
        Status: ${status}`
      );
      this.driverMarkers.set(driverId, marker);
    }
  }

  private async handleDrawRoute(payload: any): Promise<void> {

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
        opacity: 0.7,
        pane: 'routePane'
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
          icon: this.createCustomIcon('📍')
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

  private showCarIcon(): L.DivIcon {
    return L.divIcon({
      html: `<div style="width: 50px; height: 50px;">
        <img src="../../assets/icons/available-taxi.svg" style="width: 100%; height: 100%;" />
        </div>
      `,
      className: 'available-taxi-icon',
      iconSize: [50, 50],
      iconAnchor: [20, 20],
      popupAnchor: [0, -20]
    });
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.mapService.clearMap();
    this.driverService.disconnectWebSocket();
  }


}
