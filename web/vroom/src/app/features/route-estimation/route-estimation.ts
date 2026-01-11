import { Component, OnInit, ElementRef, HostListener, ViewChild, ChangeDetectorRef, Output, EventEmitter, NgModule } from '@angular/core';
import * as L from 'leaflet'
import { MapService } from '../../core/services/map.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgModel } from '@angular/forms';
import { AddressSuggestionDTO } from '../../core/models/address/address-suggestion-response.dto';
import { HttpClient } from '@angular/common/http';
import { RouteQuoteEstimationDTO } from '../../core/models/address/route-quote-estimation.dto';
import { lastValueFrom } from 'rxjs';


interface Stop {
  id: number;
  address: string;
  coords: { lat: number; lng: number } | null;
}

@Component({
  selector: 'app-route-estimation',
  standalone: true,  
  imports: [CommonModule, FormsModule],
  templateUrl: './route-estimation.html',
  styleUrl: './route-estimation.css',
})
export class RouteEstimation{
  startLocation: String = ''
  endLocation: String = ''
  stopLocations: string[] = []

  stopIdCounter = 0;
  stops: Stop[] = [];
  stopSuggestions: AddressSuggestionDTO[][] = [];
  showStopSuggestions: boolean[] = [];

  price: String = ''
  time: String = ''

  startSuggestions: AddressSuggestionDTO[] = [];
  endSuggestions: AddressSuggestionDTO[] = [];

  showStartSuggestions: boolean = false;
  showEndSuggestions: boolean =  false;

  startCoords?: { lat: number; lng: number };
  endCoords?: { lat: number; lng: number };

  calculating: boolean = false;
  error: String = ''


  @ViewChild('startInput', { static: false }) startInput?: ElementRef;
  @ViewChild('startDropdown', { static: false }) startDropdown?: ElementRef;

  @ViewChild('endInput', { static: false }) endInput?: ElementRef;
  @ViewChild('endDropdown', { static: false }) endDropdown?: ElementRef;

  @Output() startCoordsChange = new EventEmitter<{lat:number,lng:number}>();
  @Output() endCoordsChange = new EventEmitter<{lat:number,lng:number}>();
  @Output() stopsCoordsChange = new EventEmitter<Array<{ lat: number; lng: number }>>();

  constructor(private mapService: MapService,private http: HttpClient, private eRef:ElementRef, private cdr: ChangeDetectorRef) {}

  @HostListener('document:mousedown', ['$event'])
  clickout(event: MouseEvent) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.showEndSuggestions = false;
      this.showStartSuggestions = false;
      this.showStopSuggestions = this.showStopSuggestions.map(() => false);
    }
  }

  async searchStart(): Promise<void>{
    this.startCoords = undefined;
    const location: string = 'Novi Sad, '+this.startLocation.trim().toString()

    this.mapService.locationSuggestion(location).subscribe({
      next: (data: AddressSuggestionDTO[]) => {
        this.startSuggestions = data;
        this.showStartSuggestions = true;
      },
      error: (err) => {
        this.startSuggestions = [];
      }
    })
  }

  async searchEnd(): Promise<void>{
    this.endCoords = undefined;
    const location: string = 'Novi Sad, '+this.endLocation.trim().toString()

    this.mapService.locationSuggestion(location).subscribe({
      next: (data: AddressSuggestionDTO[]) => {
        this.endSuggestions = data;
        this.showEndSuggestions = true;
      },
      error: (err) => {
        this.endSuggestions = [];
      }
    })
  }

  selectStart(location: AddressSuggestionDTO): void{
    this.startLocation = String(location.label)
    this.startCoords = {lat: location.lat, lng: location.lon}
    this.startSuggestions = []
    this.showStartSuggestions = false
  }

  selectEnd(location: any): void{
    this.endLocation = String(location.label)
    this.endCoords = {lat: location.lat, lng: location.lon}
    this.endSuggestions = []
    this.showEndSuggestions = false
  }

  private async tryGeocode(type: 'start' | 'end'): Promise<boolean>{
    const address = type === 'start' ? this.startLocation.trim().toString() : this.endLocation.trim().toString();
    const query = `Novi Sad, ${address}`;

    try {
        const data = await lastValueFrom(this.mapService.geocodeLocation(query));
        
        if (data) {
            if (type === 'start') {
                this.startCoords = { lat: data.lat, lng: data.lon };
            } else {
                this.endCoords = { lat: data.lat, lng: data.lon };
            }
        }else
          return false
        return true
    } catch (error) {
        console.error(`Geocoding failed for ${type}:`, error);
        return false
    }
  }

  private async tryStopGeocode(): Promise<boolean>{
    for(let i = 0; i < this.stops.length; i++){
      if (this.stops[i].coords) continue;
      const query = `Novi Sad, ${this.stops[i].address}`;
      try {
          const data = await lastValueFrom(this.mapService.geocodeLocation(query));
          if (data) {
              this.stops[i].coords = {lat: data.lat, lng: data.lon}
          }else
            return false
      } catch (error) {
          return false
      }
    }

    return true
  }


  async onSubmit(): Promise<void>{
    console.log(this.stops)
    console.log(this.startCoords)
    console.log(this.endCoords)
    this.calculating = true;

    const startValid = this.startCoords || await this.tryGeocode('start');
    const endValid = this.endCoords || await this.tryGeocode('end');

    const stopsValid = await this.tryStopGeocode()

    if (!startValid || !endValid || !stopsValid) {
      this.calculating = false;
      this.error = 'Unable to get a quote for these locations, please try other ones or again later';
      this.cdr.detectChanges();
      return; 
    }

    console.log(this.startCoords)
    console.log(this.endCoords)
    console.log(this.stops)

    const start = this.startCoords?.lat+','+this.startCoords?.lng
    const end = this.endCoords?.lat+','+this.endCoords?.lng

    const stops = this.stops.length > 0
        ? this.stops
            .filter(stop => stop.coords)
            .map(stop => `${stop.coords!.lat},${stop.coords!.lng}`)
            .join(';')
      : undefined;


    this.mapService.routeQuote(start, end, stops).subscribe({
      next: (data: RouteQuoteEstimationDTO) => {
        this.price = String(data.price)
        this.time = String(data.time)
        this.calculating = false; 
        this.error = ''
        this.startCoordsChange.emit(this.startCoords)
        this.endCoordsChange.emit(this.endCoords)
        this.stopsCoordsChange.emit(
            this.stops
                .filter(stop => stop.coords !== null)
                .map(stop => stop.coords!)
        );
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.calculating = false; 
        this.startCoords = undefined
        this.endCoords = undefined
        this.price = ''
        this.time = ''
        this.error = 'An error occurred'
        this.cdr.detectChanges();
      }
    })
  }

  addStop() {
    this.stops.push({
      id: ++this.stopIdCounter,
      address: '',
      coords: null
    });

    this.stopSuggestions.push([]);
    this.showStopSuggestions.push(false);
  }


  selectStop(i: number, location: AddressSuggestionDTO) {
  this.stops[i].address = location.label;
  this.stops[i].coords = { lat: location.lat, lng: location.lon };
  this.showStopSuggestions[i] = false;
}

  updateStopLocation(index: number, value: string) {
    this.stops[index].address = value;
  }

  searchStop(i: any){
    const query = this.stops[i].address.trim();

    if (!query || query.length < 3) {
      this.stopSuggestions[i] = [];
      this.showStopSuggestions[i] = false;
      this.stops[i].coords = null;
      return;
    }

    const location: string = `Novi Sad, ${query}`;

    this.mapService.locationSuggestion(location).subscribe({
      next: (data: AddressSuggestionDTO[]) => {
        this.stopSuggestions[i] = data;
        this.showStopSuggestions[i] = true;
      },
      error: (err) => {
        this.endSuggestions = [];
      }
    })
  }

  removeStop(index: number) {
    this.stops.splice(index, 1);
    this.stopSuggestions.splice(index, 1);
    this.showStopSuggestions.splice(index, 1);
  }

  trackByStopId(index: number, stop: Stop): number {
    return stop.id;
  }
}
