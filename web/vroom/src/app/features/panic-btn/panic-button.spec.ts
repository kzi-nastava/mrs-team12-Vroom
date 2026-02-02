import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicButton } from './panic-button';

describe('Panic', () => {
  let component: PanicButton;
  let fixture: ComponentFixture<PanicButton>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicButton]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicButton);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
