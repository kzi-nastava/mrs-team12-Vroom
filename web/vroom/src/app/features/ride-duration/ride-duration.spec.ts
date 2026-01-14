import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideDuration } from './ride-duration';

describe('RideDuration', () => {
  let component: RideDuration;
  let fixture: ComponentFixture<RideDuration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideDuration]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideDuration);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
