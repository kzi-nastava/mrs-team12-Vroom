import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelRide } from './cancel-ride';

describe('CancelRide', () => {
  let component: CancelRide;
  let fixture: ComponentFixture<CancelRide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelRide]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelRide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
