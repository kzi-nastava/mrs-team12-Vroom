import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainView } from './main-view';

describe('MainView', () => {
  let component: MainView;
  let fixture: ComponentFixture<MainView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainView);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
