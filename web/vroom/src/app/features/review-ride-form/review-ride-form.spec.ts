import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewRideForm } from './review-ride-form';

describe('ReviewRideForm', () => {
  let component: ReviewRideForm;
  let fixture: ComponentFixture<ReviewRideForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewRideForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewRideForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
