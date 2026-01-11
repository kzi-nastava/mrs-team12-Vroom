import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RouteEstimation } from './route-estimation';

describe('RouteEstimation', () => {
  let component: RouteEstimation;
  let fixture: ComponentFixture<RouteEstimation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouteEstimation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RouteEstimation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
