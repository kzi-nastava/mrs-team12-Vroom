import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { vi, describe, it, expect, beforeEach } from 'vitest';
import { RideService } from '../../core/services/ride.service';
import { ReviewPopup } from './review-ride-form';

describe('ReviewPopup', () => {
  let component: ReviewPopup;
  let fixture: ComponentFixture<ReviewPopup>;
  let rideServiceMock: any;

  beforeEach(async () => {
    rideServiceMock = {
      leaveReviewRequest: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ReviewPopup, FormsModule],
      providers: [
        { provide: RideService, useValue: rideServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewPopup);
    component = fixture.componentInstance;
    component.rideID = '123';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should update driverRating model when star is clicked', async () => {
    const stars = fixture.debugElement.queryAll(By.css('.rating-group .star'));
    
    const fourthStar = stars[3];
    
    if (fourthStar) {
      fourthStar.triggerEventHandler('click', null);
      fixture.detectChanges();
      await fixture.whenStable();
      expect(component.driverRating).toBe(4);
    } else {
      throw new Error('Driver stars not found');
    }
  });

  it('should update carRating model when star is clicked', async () => {
    const stars = fixture.debugElement.queryAll(By.css('.rating-group .star'));
    
    const secondCarStar = stars[6];
    
    if (secondCarStar) {
      secondCarStar.triggerEventHandler('click', null);
      fixture.detectChanges();
      await fixture.whenStable();
      expect(component.carRating).toBe(2);
    } else {
      throw new Error('Car stars not found');
    }
  });

  it('should not call service if ratings are 0', () => {
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    const serviceSpy = vi.spyOn(rideServiceMock, 'leaveReviewRequest');

    component.onSubmit();

    expect(serviceSpy).not.toHaveBeenCalled();
    expect(alertSpy).toHaveBeenCalledWith('Please rate both the driver and the car');
  });

  it('should call service with correct data and close on success', async () => {
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    const closeSpy = vi.spyOn(component.closePopup, 'emit');
    rideServiceMock.leaveReviewRequest.mockReturnValue(of({ message: 'Success' }));

    component.driverRating = 5;
    component.carRating = 4;
    component.comment = 'Great';
    
    component.onSubmit();
    fixture.detectChanges();

    expect(rideServiceMock.leaveReviewRequest).toHaveBeenCalledWith('123', {
      driverRating: 5,
      vehicleRating: 4,
      comment: 'Great'
    });
    expect(alertSpy).toHaveBeenCalledWith('Review submitted successfully');
    expect(closeSpy).toHaveBeenCalled();
  });

  it('should alert error if service fails', () => {
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    rideServiceMock.leaveReviewRequest.mockReturnValue(throwError(() => new Error('API Error')));
    
    component.driverRating = 3;
    component.carRating = 3;
    component.onSubmit();

    expect(alertSpy).toHaveBeenCalledWith("Review couldn't be sent.");
  });
});