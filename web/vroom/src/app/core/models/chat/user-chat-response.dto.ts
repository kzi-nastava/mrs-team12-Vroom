import { ChatMessageResponseDTO } from "./chat-message-response.dto";

export interface UserChatResponseDTO{
    userName : String;
    profilePicture : string;
    messages : ChatMessageResponseDTO[];
}