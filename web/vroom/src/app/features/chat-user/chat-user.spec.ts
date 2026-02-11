import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatUser } from './chat-user';

describe('ChatUser', () => {
  let component: ChatUser;
  let fixture: ComponentFixture<ChatUser>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatUser]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChatUser);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
