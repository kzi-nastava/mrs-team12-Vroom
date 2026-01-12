import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelStopRide } from './cancel-stop-ride';

describe('CancelRide', () => {
  let component: CancelStopRide;
  let fixture: ComponentFixture<CancelStopRide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelStopRide]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelStopRide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
