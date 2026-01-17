import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuoteView } from './quote-view';

describe('QuoteView', () => {
  let component: QuoteView;
  let fixture: ComponentFixture<QuoteView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuoteView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(QuoteView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
