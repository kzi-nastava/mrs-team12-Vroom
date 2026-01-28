import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders, HttpClientModule } from '@angular/common/http';
import { MapService } from '../../core/services/map.service';
import { ChangeDetectorRef, NgZone } from '@angular/core';

@Component({
  selector: 'app-order-a-ride',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, DecimalPipe],
  templateUrl: './order-a-ride.html',
  styleUrls: ['./order-a-ride.css']
})
export class OrderARide implements OnInit {


  startLocation = '';
  endLocation = '';
  vehicleType = 'STANDARD';
  childrenAllowed = false;
  petsAllowed = false;
  otherEmail = '';
  scheduledTime: string | null = null;

  startSuggestions: any[] = [];
  endSuggestions: any[] = [];
  showStartSuggestions = false;
  showEndSuggestions = false;

  stops: any[] = [];
  stopSuggestions: any[][] = [];
  showStopSuggestions: boolean[] = [];

  startCoords?: { lat: number; lng: number };
  endCoords?: { lat: number; lng: number };

  time: number | null = null;
  price: number | null = null;
  calculating = false;
  error = '';

  routeResult: any = null;

  private stopIdCounter = 0;

  constructor(
    private mapService: MapService,
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) {}

  ngOnInit() {
    setTimeout(() => {
      this.mapService.showVehicles();
    }, 300);
  }


  searchStart() {
    if (!this.startLocation || this.startLocation.length < 2) {
      this.startSuggestions = [];
      this.showStartSuggestions = false;
      return;
    }

    this.mapService.locationSuggestion('Novi Sad, ' + this.startLocation)
      .subscribe(data => {
        this.startSuggestions = data;
        this.showStartSuggestions = data.length > 0;
      });
  }

  selectStart(s: any) {
    this.startLocation = s.label;
    this.startCoords = { lat: s.lat, lng: s.lon };
    this.startSuggestions = [];
    this.showStartSuggestions = false;
  }

  searchEnd() {
    if (!this.endLocation || this.endLocation.length < 2) {
      this.endSuggestions = [];
      this.showEndSuggestions = false;
      return;
    }

    this.mapService.locationSuggestion('Novi Sad, ' + this.endLocation)
      .subscribe(data => {
        this.endSuggestions = data;
        this.showEndSuggestions = data.length > 0;
      });
  }

  selectEnd(s: any) {
    this.endLocation = s.label;
    this.endCoords = { lat: s.lat, lng: s.lon };
    this.endSuggestions = [];
    this.showEndSuggestions = false;
  }

  searchStop(i: number) {
    const query = this.stops[i]?.address;

    if (!query || query.length < 2) {
      this.stopSuggestions[i] = [];
      this.showStopSuggestions[i] = false;
      return;
    }

    this.mapService.locationSuggestion('Novi Sad, ' + query)
      .subscribe(data => {
        this.stopSuggestions[i] = data;
        this.showStopSuggestions[i] = data.length > 0;
      });
  }

  selectStop(i: number, s: any) {
    this.stops[i].address = s.label;
    this.stops[i].coords = { lat: s.lat, lng: s.lon };
    this.stopSuggestions[i] = [];
    this.showStopSuggestions[i] = false;
  }


  addStop() {
    this.stops.push({ id: this.stopIdCounter++, address: '', coords: null });
    this.stopSuggestions.push([]);
    this.showStopSuggestions.push(false);
  }

  removeStop(i: number) {
    this.stops.splice(i, 1);
    this.stopSuggestions.splice(i, 1);
    this.showStopSuggestions.splice(i, 1);
  }

  trackByStopId(index: number, stop: any) {
    return stop.id;
  }


  calculateRoute() {
    this.error = '';

    if (!this.startCoords || !this.endCoords) {
      this.error = 'Please select start and end first';
      return;
    }

    this.calculating = true;
    this.time = null;
    this.price = null;

    const start = `${this.startCoords.lat},${this.startCoords.lng}`;
    const end = `${this.endCoords.lat},${this.endCoords.lng}`;
const stops = this.stops.length > 0
    ? this.stops
        .filter(stop => stop.coords)
        .map(stop => `${stop.coords!.lat},${stop.coords!.lng}`)
        .join(';')
    : undefined;

    this.mapService.routeQuote(start, end, stops).subscribe({
      next: quote => {
        console.log("Quote received:", quote);
        if (!quote || quote.price == null || quote.time == null) {
          this.error = 'Invalid route!';
          this.calculating = false;
          return;
        }

        this.price = quote.price;
        this.time = quote.time;

        this.routeResult = {
          estimatedTimeMin: quote.time,
          price: quote.price
        };

        const payload = {
          start: this.startCoords,
          end: this.endCoords,
          stops: this.stops.filter(s => s.coords).map(s => s.coords)
        };

this.mapService.getRouteCoordinates(payload)
  .then(route => {
    this.zone.run(() => {
      if (route) {
        this.mapService.drawRoute(
          payload.start!,
          payload.end!,
          payload.stops
        );
      }

      this.calculating = false;
      this.cdr.detectChanges();
    });
  })
  .catch(err => {
    this.zone.run(() => {
      console.error('Error fetching route', err);
      this.error = 'Could not fetch route details';
      this.calculating = false;
      this.cdr.detectChanges();
    });
  });
      },

      error: err => {
        console.error('Error calculating quote', err);
        this.error = 'Could not calculate route';
        this.calculating = false;
      }
    });
  }


  orderRide() {
  const token = localStorage.getItem('jwt');
  if (!token) {
    alert('You are not logged in!');
    return;
  }

  if (!this.routeResult) {
    alert('Please calculate route first!');
    return;
  }
  let scheduled = false;
  let scheduledTimeISO: string | null = null;

  if (this.scheduledTime) {
    const [hours, minutes] = this.scheduledTime.split(':').map(Number);

    const now = new Date();
    const scheduledDate = new Date();
    scheduledDate.setHours(hours, minutes, 0, 0);

    if (scheduledDate < now) {
      alert('Scheduled time cannot be in the past');
      return;
    }

    const maxDate = new Date();
    maxDate.setHours(maxDate.getHours() + 5);

    if (scheduledDate > maxDate) {
      alert('Scheduled time cannot be more than 5 hours ahead');
      return;
    }

    scheduled = true;
    scheduledTimeISO = scheduledDate.toISOString();
  }

  const rideRequest = {
    locations: [
      this.startLocation,
      ...this.stops.map(s => s.address),
      this.endLocation
    ],
    passengersEmails: this.otherEmail
      ? [localStorage.getItem('email'), this.otherEmail]
      : [localStorage.getItem('email')],
    vehicleType: this.vehicleType,
    babiesAllowed: this.childrenAllowed,
    petsAllowed: this.petsAllowed,
    scheduled: scheduled,
    scheduledTime: scheduledTimeISO,
    route: {
      startLocationLat: this.startCoords?.lat,
      startLocationLng: this.startCoords?.lng,
      endLocationLat: this.endCoords?.lat,
      endLocationLng: this.endCoords?.lng,
      stops: (this.stops?.filter(s => s.coords).map(s => ({
        lat: s.coords.lat,
        lng: s.coords.lng
      }))) || []
    }
  };

  const headers = new HttpHeaders({
    'Authorization': 'Bearer ' + token,
    'Content-Type': 'application/json'
  });

  this.error = '';
  console.log('Sending ride request:', rideRequest);
  this.http.post('http://localhost:8080/api/rides', rideRequest, { headers })
    .subscribe({
      next: () => {
        this.error = '';
        alert('Ride successfully created!');
      },
      error: err => {
        console.error('Order ride error:', err);

        if (err.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = 'Could not create ride. Please try again.';
        }

        this.cdr.detectChanges();
      }
    });
}


  getMinTime(): string {
  const now = new Date();
  return now.toISOString().substring(11, 16); 
}

getMaxTime(): string {
  const max = new Date();
  max.setHours(max.getHours() + 5);
  return max.toISOString().substring(11, 16); 
}
}
