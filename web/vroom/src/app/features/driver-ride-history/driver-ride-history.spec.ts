import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRideHistory } from './driver-ride-history';

describe('DriverRideHistory', () => {
  let component: DriverRideHistory;
  let fixture: ComponentFixture<DriverRideHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverRideHistory]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRideHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
