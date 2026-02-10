import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService } from '../../core/services/chat.service';
import { ChatMessageResponseDTO } from '../../core/models/chat/chat-message-response.dto';
import { ChatMessageRequestDTO } from '../../core/models/chat/chat-message-request.dto';

@Component({
  selector: 'app-chat-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-user.html',
  styleUrl: './chat-user.css'
})
export class ChatUser implements OnInit, OnDestroy {
  messages: ChatMessageResponseDTO[] = [];
  newMessage: string = '';
  private messageSub?: Subscription;

  constructor(private chatService: ChatService,
    private cdr : ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.chatService.getUserChat().subscribe(history => {
      this.messages = history;
      this.cdr.detectChanges();
    });

    this.messageSub = this.chatService.getMessageStream().subscribe(message => {
      this.messages.push(message);
    });
  }

  sendMessage() {
    if (!this.newMessage.trim()) return;

    const temporaryMessage: ChatMessageResponseDTO = {
      content: this.newMessage,
      senderName: 'Me',
      sentByAdmin: false,
      timestamp: new Date()
    };

    this.messages.push(temporaryMessage);
    this.chatService.sendUserMessage(this.newMessage);
    this.newMessage = '';
    this.cdr.detectChanges();
  }

  ngOnDestroy() {
    this.messageSub?.unsubscribe();
  }
}