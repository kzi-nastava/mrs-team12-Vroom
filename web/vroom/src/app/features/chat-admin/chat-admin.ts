import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService } from '../../core/services/chat.service';
import { ChatMessageResponseDTO } from '../../core/models/chat/chat-message-response.dto';
import { ChatResponseDTO } from '../../core/models/chat/chat-response.dto';
import { NgToastService } from 'ng-angular-popup';

@Component({
  selector: 'app-chat-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-admin.html',
  styleUrl: './chat-admin.css'
})
export class ChatAdmin implements OnInit, OnDestroy {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;
  chats: ChatResponseDTO[] = [];
  selectedChatId: number | null = null;
  messagesMap: Map<number, ChatMessageResponseDTO[]> = new Map();
  newMessage: string = '';
  private messageSub?: Subscription;
  profilePicture: string = '';

  constructor(private chatService: ChatService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadAllChats();
    this.messageSub = this.chatService.getMessageStream().subscribe(message => {
      this.handleIncomingMessage(message);
      this.cdr.detectChanges();
      this.scrollToBottom();
    });
  }

  loadAllChats() {
    this.chatService.getAllChats().subscribe(data => {
      this.chats = data;
      this.sortChats();
      this.cdr.detectChanges();
    });
  }

  selectChat(chatId: number) {
    this.selectedChatId = chatId;
    if (!this.messagesMap.has(this.selectedChatId)) {
      this.chatService.getAdminChat(this.selectedChatId.toString()).subscribe(history => {
        this.messagesMap.set(this.selectedChatId!, history);
        this.cdr.detectChanges();
        this.scrollToBottom();
      });
    } else {
      this.cdr.detectChanges();
      this.scrollToBottom();
    }
  }

  private handleIncomingMessage(message: ChatMessageResponseDTO) {
    const chatId = message.chatID;
    const currentMessages = this.messagesMap.get(chatId) || [];
    this.messagesMap.set(chatId, [...currentMessages, message]);

    const chatIndex = this.chats.findIndex(c => c.chatId === chatId);
    if (chatIndex !== -1) {
      this.chats[chatIndex].lastMessageTime = message.timestamp;
    } else {
      const newChat: ChatResponseDTO = {
        chatId: Number(chatId),
        userName: message.senderName,
        lastMessageTime: message.timestamp
      };
      this.chats.push(newChat);
    }
    this.sortChats();
    this.cdr.detectChanges();
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.selectedChatId) return;

    const temporaryMessage: ChatMessageResponseDTO = {
      chatID: this.selectedChatId,
      content: this.newMessage,
      senderName: 'Admin',
      sentByAdmin: true,
      timestamp: new Date(),
      profilePicture: this.profilePicture
    };

    const currentMessages = this.messagesMap.get(this.selectedChatId) || [];
    this.messagesMap.set(this.selectedChatId, [...currentMessages, temporaryMessage]);
    
    const chatIndex = this.chats.findIndex(c => c.chatId === this.selectedChatId);
    if (chatIndex !== -1) {
      this.chats[chatIndex].lastMessageTime = temporaryMessage.timestamp;
      this.sortChats();
    }

    this.chatService.sendAdminMessage(this.selectedChatId.toString(), this.newMessage);
    this.newMessage = '';
    this.cdr.detectChanges();
    this.scrollToBottom();
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.scrollContainer?.nativeElement) {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }

  isToday(date: Date | string): boolean {
    const messageDate = new Date(date);
    const today = new Date();
    return messageDate.getDate() === today.getDate() &&
          messageDate.getMonth() === today.getMonth() &&
          messageDate.getFullYear() === today.getFullYear();
  }

  get currentMessages(): ChatMessageResponseDTO[] {
    return this.selectedChatId ? (this.messagesMap.get(this.selectedChatId) || []) : [];
  }

  ngOnDestroy() {
    this.messageSub?.unsubscribe();
    this.messagesMap.clear();
  }

  private sortChats() {
    this.chats.sort((a, b) => {
      const timeA = new Date(a.lastMessageTime).getTime();
      const timeB = new Date(b.lastMessageTime).getTime();
      return timeB - timeA;
    });
  }

}