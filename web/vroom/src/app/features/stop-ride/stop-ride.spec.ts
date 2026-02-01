import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopRide } from './stop-ride';

describe('StopRide', () => {
  let component: StopRide;
  let fixture: ComponentFixture<StopRide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StopRide]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StopRide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
