import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { ChatService } from '../../core/services/chat.service';
import { ChatMessageResponseDTO } from '../../core/models/chat/chat-message-response.dto';

@Component({
  selector: 'app-chat-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat-user.html',
  styleUrl: './chat-user.css'
})
export class ChatUser implements OnInit, OnDestroy {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;
  messages: ChatMessageResponseDTO[] = [];
  newMessage: string = '';
  private messageSub?: Subscription;
  chatID : number = -1;
  profilePicture : string = '';
  userName : String = '';


  constructor(private chatService: ChatService,
    private cdr : ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.chatService.getUserChat().subscribe(history => {
      if (history){
        this.messages = history.messages;
        this.profilePicture = history.profilePicture;
        this.userName = history.userName;
      }
      this.cdr.detectChanges();
      this.scrollToBottom();
    });
    this.messageSub = this.chatService.getMessageStream().subscribe(message => {
      this.messages.push(message);
      this.scrollToBottom();
      this.cdr.detectChanges()
    });
  }

  sendMessage() {
    const userIdStr = localStorage.getItem("user_id");
    if (!this.newMessage.trim() || !userIdStr) return;
    const id = Number(userIdStr)
    const temporaryMessage: ChatMessageResponseDTO = {
      chatID: id,
      content: this.newMessage,
      senderName: 'Me',
      sentByAdmin: false,
      timestamp: new Date(),
      profilePicture: this.profilePicture
    };
    this.messages.push(temporaryMessage);
    this.chatService.sendUserMessage(this.newMessage);
    this.newMessage = '';
    this.cdr.detectChanges();
    this.scrollToBottom()
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      if (this.scrollContainer?.nativeElement) {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
      }
    }, 100); 
  }

  ngOnDestroy() {
    this.messageSub?.unsubscribe();
  }

  isToday(date: Date | string): boolean {
    const messageDate = new Date(date);
    const today = new Date();
    return messageDate.getDate() === today.getDate() &&
          messageDate.getMonth() === today.getMonth() &&
          messageDate.getFullYear() === today.getFullYear();
  }



}