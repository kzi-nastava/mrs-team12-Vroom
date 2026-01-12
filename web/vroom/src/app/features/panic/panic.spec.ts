import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Panic } from './panic';

describe('Panic', () => {
  let component: Panic;
  let fixture: ComponentFixture<Panic>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Panic]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Panic);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
