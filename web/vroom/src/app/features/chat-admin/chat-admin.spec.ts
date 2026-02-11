import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatAdmin } from './chat-admin';

describe('ChatAdmin', () => {
  let component: ChatAdmin;
  let fixture: ComponentFixture<ChatAdmin>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatAdmin]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatAdmin);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
