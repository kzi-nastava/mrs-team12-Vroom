import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService } from '../../core/services/chat.service';
import { ChatMessageResponseDTO } from '../../core/models/chat/chat-message-response.dto';
import { ChatResponseDTO } from '../../core/models/chat/chat-response.dto';

@Component({
  selector: 'app-chat-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-admin.html',
  styleUrl: './chat-admin.css'
})
export class ChatAdmin implements OnInit, OnDestroy {
  chats: ChatResponseDTO[] = [];
  selectedChatId: string | null = null;
  messagesMap: Map<string, ChatMessageResponseDTO[]> = new Map();
  newMessage: string = '';
  private messageSub?: Subscription;

  constructor(private chatService: ChatService, private cdr : ChangeDetectorRef) {}

  ngOnInit() {
    this.loadAllChats();
    this.messageSub = this.chatService.getMessageStream().subscribe(message => {
      this.handleIncomingMessage(message);
    });
  }

  loadAllChats() {
    this.chatService.getAllChats().subscribe(data => {
      this.chats = data;
      this.cdr.detectChanges();
    });
  }

  selectChat(chatId: number) {
    this.selectedChatId = chatId.toString();
    if (!this.messagesMap.has(this.selectedChatId)) {
      this.chatService.getAdminChat(this.selectedChatId).subscribe(history => {
        this.messagesMap.set(this.selectedChatId!, history);
      });
    }
  }

  private handleIncomingMessage(message: ChatMessageResponseDTO) {
    const chatId = message.senderName;
    const currentMessages = this.messagesMap.get(chatId) || [];
    this.messagesMap.set(chatId, [...currentMessages, message]);

    const chatRef = this.chats.find(c => c.userName === message.senderName);
    if (chatRef) {
      chatRef.lastMessageTime = new Date();
    }
    this.cdr.detectChanges();
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.selectedChatId) return;

    const temporaryMessage: ChatMessageResponseDTO = {
      content: this.newMessage,
      senderName: 'Me',
      sentByAdmin: true,
      timestamp: new Date()
    };

    const currentMessages = this.messagesMap.get(this.selectedChatId) || []
    this.messagesMap.set(this.selectedChatId, [...currentMessages, temporaryMessage])
    this.chatService.sendAdminMessage(this.selectedChatId, this.newMessage);
    this.newMessage = '';
    this.cdr.detectChanges();
  }

  get currentMessages(): ChatMessageResponseDTO[] {
    return this.selectedChatId ? (this.messagesMap.get(this.selectedChatId) || []) : [];
  }

  ngOnDestroy() {
    this.messageSub?.unsubscribe();
    this.messagesMap.clear();
  }
}