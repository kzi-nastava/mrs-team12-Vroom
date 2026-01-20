import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideDurationDriver } from './ride-duration-driver';

describe('RideDurationDriver', () => {
  let component: RideDurationDriver;
  let fixture: ComponentFixture<RideDurationDriver>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideDurationDriver]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideDurationDriver);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
