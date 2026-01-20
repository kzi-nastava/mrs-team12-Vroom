import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideEnd } from './ride-end';

describe('RideEnd', () => {
  let component: RideEnd;
  let fixture: ComponentFixture<RideEnd>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideEnd]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideEnd);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
