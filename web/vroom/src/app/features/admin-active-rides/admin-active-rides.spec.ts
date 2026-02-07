import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminActiveRides } from './admin-active-rides';

describe('AdminActiveRides', () => {
  let component: AdminActiveRides;
  let fixture: ComponentFixture<AdminActiveRides>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminActiveRides]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminActiveRides);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
