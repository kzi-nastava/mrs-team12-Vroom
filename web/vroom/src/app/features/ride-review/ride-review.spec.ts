import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideReview } from './ride-review';

describe('RideReview', () => {
  let component: RideReview;
  let fixture: ComponentFixture<RideReview>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideReview]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideReview);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
