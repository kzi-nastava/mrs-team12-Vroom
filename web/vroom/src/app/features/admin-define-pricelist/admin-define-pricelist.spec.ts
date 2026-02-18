import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDefinePricelist } from './admin-define-pricelist';

describe('AdminDefinePricelist', () => {
  let component: AdminDefinePricelist;
  let fixture: ComponentFixture<AdminDefinePricelist>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDefinePricelist]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDefinePricelist);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
