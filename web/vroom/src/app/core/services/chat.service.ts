import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, Subject, first, tap } from "rxjs";
import { ChatMessageResponseDTO } from "../models/chat/chat-message-response.dto";
import { ChatResponseDTO } from "../models/chat/chat-response.dto";
import { SocketProviderService } from "./socket-provider.service";
import { ChatMessageRequestDTO } from "../models/chat/chat-message-request.dto";
import { UserChatResponseDTO } from "../models/chat/user-chat-response.dto";

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private api = "http://localhost:8080/api/chat";
  private messageSubject = new Subject<ChatMessageResponseDTO>();

  constructor(
    private http: HttpClient,
    private socketProvider: SocketProviderService
  ) {}

  getAdminChat(chatID: string): Observable<ChatMessageResponseDTO[]> {
    return this.http.get<ChatMessageResponseDTO[]>(`${this.api}/get-admin-chat/${chatID}`);
  }

  getUserChat(): Observable<UserChatResponseDTO> {
    return this.http.get<UserChatResponseDTO>(`${this.api}/get-user-chat`);
  }

  getAllChats(): Observable<ChatResponseDTO[]> {
    return this.http.get<ChatResponseDTO[]>(`${this.api}/get-all-chats`);
  }

  getMessageStream(): Observable<ChatMessageResponseDTO> {
    return this.messageSubject.asObservable();
  }

  initAdminChatWebSocket(): Observable<void> {
    return this.socketProvider.onConnected.pipe(
      first(),
      tap(() => {
        this.socketProvider.stompClient.subscribe(`/socket-publisher/user-messages`, (msg: any) => {
          this.messageSubject.next(JSON.parse(msg.body));
        });
      })
    );
  }

  initUserChatWebSocket(userID: string): Observable<void> {
    return this.socketProvider.onConnected.pipe(
      first(),
      tap(() => {
        this.socketProvider.stompClient.subscribe(`/socket-publisher/admin-messages/${userID}`, (msg: any) => {
          this.messageSubject.next(JSON.parse(msg.body));
        });
      })
    );
  }

  sendAdminMessage(chatId: string, content: string) {
    const payload : ChatMessageRequestDTO={
        content : content,
        timeSent : new Date(),
        sentByAdmin: true
    }
    this.socketProvider.send(`/socket-subscriber/admin-send-message/${chatId}`,  payload );
  }

  sendUserMessage(content: string) {
    const payload : ChatMessageRequestDTO ={
        content : content,
        timeSent : new Date(),
        sentByAdmin: false
    }
    this.socketProvider.send(`/socket-subscriber/user-send-message`,  payload );
    console.log(payload)
  }
}