import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicFeed } from './panic-feed';

describe('PanicFeed', () => {
  let component: PanicFeed;
  let fixture: ComponentFixture<PanicFeed>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicFeed]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicFeed);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
