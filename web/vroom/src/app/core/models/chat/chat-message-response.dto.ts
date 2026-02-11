export interface ChatMessageResponseDTO{
    senderName : string;
    content : string;
    timestamp : Date;
    sentByAdmin : boolean;
    chatID : number;
    profilePicture : string;
}