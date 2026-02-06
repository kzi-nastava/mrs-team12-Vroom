import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminHomePage } from './admin-home-page';

describe('AdminHomePage', () => {
  let component: AdminHomePage;
  let fixture: ComponentFixture<AdminHomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminHomePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminHomePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
