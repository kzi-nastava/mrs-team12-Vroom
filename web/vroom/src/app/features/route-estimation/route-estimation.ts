import { Component, OnInit, ElementRef, HostListener, ViewChild, ChangeDetectorRef, Output, EventEmitter, NgModule, OnDestroy } from '@angular/core';
import * as L from 'leaflet'
import { MapService } from '../../core/services/map.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgModel } from '@angular/forms';
import { AddressSuggestionDTO } from '../../core/models/address/response/address-suggestion-response.dto';
import { HttpClient } from '@angular/common/http';
import { RouteQuoteEstimationDTO } from '../../core/models/address/response/route-quote-estimation.dto';
import { catchError, debounceTime, distinctUntilChanged, forkJoin, lastValueFrom, map, Observable, of, Subject, switchMap, takeUntil } from 'rxjs';
import { Stop } from '../../core/models/address/interfaces/stop-point.interface';
import { NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-route-estimation',
  standalone: true,  
  imports: [CommonModule, FormsModule],
  templateUrl: './route-estimation.html',
  styleUrl: './route-estimation.css',
})
export class RouteEstimation implements OnInit, OnDestroy{
  private startSearchSubject = new Subject<string>()
  private endSearchSubject = new Subject<string>()
  private stopSearchSubject = new Subject<{index: number, query: string}>()
  private destroy$ = new Subject<void>()

  startLocation: String = ''
  endLocation: String = ''
  stopLocations: string[] = []

  stopIdCounter = 0;
  stops: Stop[] = [];
  stopSuggestions: AddressSuggestionDTO[][] = [];
  showStopSuggestions: boolean[] = [];

  price: number | null = null;
  time: number | null = null;

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

  constructor(
    private mapService: MapService,
    private eRef:ElementRef, 
    private cdr: ChangeDetectorRef, 
    private toastService: NgToastService
  ) {}

  ngOnInit(): void {
      this.startSearchSubject.pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => this.mapService.locationSuggestion('Novi Sad, ' + query))
      ).subscribe(data => {
        this.startSuggestions = data;
        this.showStartSuggestions = true;
        this.cdr.detectChanges();
      });

      this.endSearchSubject.pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(query => this.mapService.locationSuggestion('Novi Sad, ' + query))
      ).subscribe(data => {
        this.endSuggestions = data;
        this.showEndSuggestions = true;
        this.cdr.detectChanges();
      });

      this.stopSearchSubject.pipe(
        takeUntil(this.destroy$),
        debounceTime(300),
        distinctUntilChanged((prev, curr) => prev.query === curr.query && prev.index === curr.index),
        switchMap(data => 
          this.mapService.locationSuggestion('Novi Sad, ' + data.query).pipe(
            map(suggestions => ({ suggestions, index: data.index }))
          )
        )
      ).subscribe(result => {
        this.stopSuggestions[result.index] = result.suggestions;
        this.showStopSuggestions[result.index] = true;
        this.cdr.detectChanges();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  @HostListener('document:mousedown', ['$event'])
  clickout(event: MouseEvent) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.showEndSuggestions = false;
      this.showStartSuggestions = false;
      this.showStopSuggestions = this.showStopSuggestions.map(() => false);
    }
  }

  searchStart(): void{
    this.startCoords = undefined;
    const location: string = 'Novi Sad, '+this.startLocation.trim().toString()
    if(this.startLocation.trim().length < 3) return

    this.startSearchSubject.next(location)
  }

  searchEnd(): void{
    this.endCoords = undefined;
    const location: string = 'Novi Sad, '+this.endLocation.trim().toString()
    if(this.endLocation.trim().length < 3) return

    this.endSearchSubject.next(location)
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

  private tryGeocode(type: 'start' | 'end'): Observable<boolean>{
    const address = type === 'start' ? this.startLocation.trim().toString() : this.endLocation.trim().toString();
    const query = `Novi Sad, ${address}`;

    return this.mapService.geocodeLocation(`Novi Sad, ${address}`).pipe(
      map(data => {
        if (data) {
          const coords = { lat: data.lat, lng: data.lon };
          type === 'start' ? this.startCoords = coords : this.endCoords = coords;
          return true;
        }
        return false;
      }),
      catchError(() => of(false))
    );
  }

  private tryStopGeocode(): Observable<boolean>{
    const stopTasks = this.stops.map((stop, i) => {
    if (stop.coords) return of(true);
    
    return this.mapService.geocodeLocation(`Novi Sad, ${stop.address}`).pipe(
        map(data => {
          if (data) {
            this.stops[i].coords = { lat: data.lat, lng: data.lon };
            return true;
          }
          return false;
        }),
        catchError(() => of(false))
      );
    });

    if (stopTasks.length === 0) return of(true);
    
    return forkJoin(stopTasks).pipe(
      map(results => results.every(res => res === true))
    );
  }


  onSubmit(): void{
    this.calculating = true;
    this.error = '';

    const startCheck$ = this.startCoords ? of(true) : this.tryGeocode('start');
    const endCheck$ = this.endCoords ? of(true) : this.tryGeocode('end');
    const stopsCheck$ = this.tryStopGeocode();

    forkJoin([startCheck$, endCheck$, stopsCheck$]).pipe(
      takeUntil(this.destroy$),
      switchMap(([startValid, endValid, stopsValid]) => {
        if (!startValid || !endValid || !stopsValid) {
          throw new Error('invalid_locations');
        }

        const start = `${this.startCoords?.lat},${this.startCoords?.lng}`;
        const end = `${this.endCoords?.lat},${this.endCoords?.lng}`;
        const stops = this.stops.length > 0
          ? this.stops.filter(s => s.coords).map(s => `${s.coords!.lat},${s.coords!.lng}`).join(';')
          : undefined;

        return this.mapService.routeQuote(start, end, stops);
      })
    ).subscribe({
      next: (data: RouteQuoteEstimationDTO) => {
        this.price = data.price;
        this.time = data.time;
        this.calculating = false;

        this.mapService.drawRoute(
          this.startCoords, 
          this.endCoords, 
          this.stops.filter(s => s.coords).map(s => s.coords!)
        );
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.calculating = false;
        if (err.message === 'invalid_locations') {
          this.error = 'Unable to get a quote for these locations...';
        } else {
          this.error = 'An error occurred';
        }

        this.toastService.danger(
          this.error.toString(),
          'Error',
          5000,
          true,
          true,
          false
        )
        this.cdr.detectChanges();
      }
    });
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
      this.cdr.detectChanges();
      return;
    }

    const location: string = `Novi Sad, ${query}`;

    this.stopSearchSubject.next({ index: i, query: query });
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
