import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeDriverStatus } from './change-driver-status';

describe('ChangeDriverStatus', () => {
  let component: ChangeDriverStatus;
  let fixture: ComponentFixture<ChangeDriverStatus>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeDriverStatus]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeDriverStatus);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
