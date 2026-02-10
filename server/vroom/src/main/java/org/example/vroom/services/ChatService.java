package org.example.vroom.services;

import org.example.vroom.DTOs.requests.chat.ChatMessageRequestDTO;
import org.example.vroom.DTOs.responses.chat.ChatMessageResponseDTO;
import org.example.vroom.DTOs.responses.chat.ChatResponseDTO;
import org.example.vroom.entities.Chat;
import org.example.vroom.entities.Message;
import org.example.vroom.entities.User;
import org.example.vroom.exceptions.chat.ChatNotFoundException;
import org.example.vroom.exceptions.user.UserNotFoundException;
import org.example.vroom.mappers.ChatMapper;
import org.example.vroom.repositories.ChatRepository;
import org.example.vroom.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatMapper chatMapper;

    public List<ChatMessageResponseDTO> getMessagesByUserId(Long userId) {
        Optional<Chat> chatOptional = chatRepository.findByUserId(userId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            List<Message> messages = chat.getMessages();
            List<ChatMessageResponseDTO> responseDTOs = new ArrayList<>();
            for (Message message : messages) {
                responseDTOs.add(chatMapper.mapToDTO(message));
            }
            return responseDTOs;
        }
        return new ArrayList<>();
    }

    public List<ChatMessageResponseDTO> getMessagesByChatId(Long chatId) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isPresent()) {
            Chat chat = chatOptional.get();
            List<Message> messages = chat.getMessages();
            List<ChatMessageResponseDTO> responseDTOs = new ArrayList<>();
            for (Message message : messages) {
                responseDTOs.add(chatMapper.mapToDTO(message));
            }
            return responseDTOs;
        }
        return new ArrayList<>();
    }


    @Transactional
    public ChatMessageResponseDTO userSendMessage(Long userId, ChatMessageRequestDTO messageRequestDTO) {
        System.out.println("USER SENDING MESSAGE =================================================================");
        Optional<Chat> chatOptional = chatRepository.findByUserId(userId);
        Chat chat = new Chat();
        if (chatOptional.isPresent()) {
            chat = chatOptional.get();
        }else{
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isEmpty()) {
                throw new UserNotFoundException("User not found");
            }
            chat.setUser(userOptional.get());
            chat.setMessages(new ArrayList<>());
        }
        Message message = chatMapper.dtoToMessage(messageRequestDTO);
        message.setSenderName(chat.getUser().getFirstName() + " " + chat.getUser().getLastName());
        chat.getMessages().add(message);
        chat.setLastMessageTime(message.getTimeSent());
        chatRepository.saveAndFlush(chat);
        return chatMapper.mapToDTO(message);
    }

    @Transactional
    public ChatMessageResponseDTO adminSendMessage(Long chatID, ChatMessageRequestDTO messageRequestDTO) {
        Optional<Chat> chatOptional = chatRepository.findById(chatID);
        if (chatOptional.isEmpty()) {
            throw new ChatNotFoundException("Chat not found");
        }
        Chat chat = chatOptional.get();
        Message message = chatMapper.dtoToMessage(messageRequestDTO);
        message.setSenderName("Administrator");
        chat.getMessages().add(message);
        chat.setLastMessageTime(message.getTimeSent());
        chatRepository.saveAndFlush(chat);
        return chatMapper.mapToDTO(message);
    }

    public Long getUserId(Long chatId) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);
        if (chatOptional.isPresent()) {
            return chatOptional.get().getUser().getId();
        }
        return null;
    }

    public List<ChatResponseDTO> getAllChats() {
        List<Chat> chats = chatRepository.findAll();
        List<ChatResponseDTO> responseDTOs = new ArrayList<>();
        for (Chat chat : chats) {
            responseDTOs.add(chatMapper.chatToDTO(chat));
        }
        return responseDTOs;
    }


}
