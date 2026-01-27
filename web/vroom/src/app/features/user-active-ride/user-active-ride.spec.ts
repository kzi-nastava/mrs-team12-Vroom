import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserActiveRide } from './user-active-ride';

describe('UserActiveRide', () => {
  let component: UserActiveRide;
  let fixture: ComponentFixture<UserActiveRide>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserActiveRide]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserActiveRide);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
