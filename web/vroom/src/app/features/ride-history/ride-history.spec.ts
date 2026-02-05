import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistory } from './ride-history';

describe('RideHistory', () => {
  let component: RideHistory;
  let fixture: ComponentFixture<RideHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistory]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
