import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DriverActiveRide } from './driver-active-ride';

describe('DriverActiveRide', () => {
  let component: DriverActiveRide;
  let fixture: ComponentFixture<DriverActiveRide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverActiveRide]
    }).compileComponents();

    fixture = TestBed.createComponent(DriverActiveRide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
