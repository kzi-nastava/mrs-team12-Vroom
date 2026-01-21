import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Socket } from './socket';

describe('Socket', () => {
  let component: Socket;
  let fixture: ComponentFixture<Socket>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Socket]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Socket);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
