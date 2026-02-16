package com.example.vroom.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vroom.DTOs.chat.request.ChatMessageRequestDTO;
import com.example.vroom.DTOs.chat.response.ChatMessageResponseDTO;
import com.example.vroom.DTOs.chat.response.ChatResponseDTO;
import com.example.vroom.DTOs.chat.response.UserChatResponseDTO;
import com.example.vroom.DTOs.ride.responses.RideUpdateResponseDTO;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.enums.RideStatus;
import com.example.vroom.network.RetrofitClient;
import com.example.vroom.network.SocketProvider;
import com.example.vroom.services.ChatService;
import com.google.gson.Gson;

import java.net.Socket;
import java.time.LocalDateTime;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<ChatResponseDTO>> allChats = new MutableLiveData<>();
    public LiveData<List<ChatResponseDTO>> getAllChats() {return allChats;}
    private final MutableLiveData<UserChatResponseDTO> userChat = new MutableLiveData<>();
    public LiveData<UserChatResponseDTO> getUserChat() { return userChat; }
    private final MutableLiveData<ChatMessageResponseDTO> incomingMessage = new MutableLiveData<>();
    private final MutableLiveData<List<ChatMessageResponseDTO>> allMessages = new MutableLiveData<>();
    public LiveData<List<ChatMessageResponseDTO>> getAllMessages() {return allMessages;}
    public LiveData<ChatMessageResponseDTO> getIncomingMessage() { return incomingMessage; }
    private final Gson gson = new Gson();
    private Disposable messageSubscription;

    public void loadUserChat(){
        RetrofitClient.getChatService().getUserChat().enqueue(new Callback<UserChatResponseDTO>() {
            @Override
            public void onResponse(Call<UserChatResponseDTO> call, Response<UserChatResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null){
                    userChat.postValue(response.body());
                    allMessages.postValue(response.body().getMessages());
                }
            }

            @Override
            public void onFailure(Call<UserChatResponseDTO> call, Throwable t) {

            }
        });
    }

    public void loadAdminChat(long chatID){
        RetrofitClient.getChatService().getAdminChat(chatID).enqueue(new Callback<List<ChatMessageResponseDTO>>() {
            @Override
            public void onResponse(Call<List<ChatMessageResponseDTO>> call, Response<List<ChatMessageResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null){
                    allMessages.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<ChatMessageResponseDTO>> call, Throwable t) {
            }
        });
    }

    public void loadAllChats(){
        RetrofitClient.getChatService().getAllChats().enqueue(new Callback<List<ChatResponseDTO>>() {
            @Override
            public void onResponse(Call<List<ChatResponseDTO>> call, Response<List<ChatResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null){
                    allChats.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<ChatResponseDTO>> call, Throwable t) {
            }
        });
    }

    public void sendAdminMessage(Long chatID, String content){
        ChatMessageRequestDTO dto = new ChatMessageRequestDTO(LocalDateTime.now().toString(),true, content);
        SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/admin-send-message/" + chatID, gson.toJson(dto))
                .subscribe();
        ChatMessageResponseDTO localUpdate = new ChatMessageResponseDTO(
                chatID, "Me", content, LocalDateTime.now().toString(), true, ""
        );
        incomingMessage.postValue(localUpdate);
    }

    public void sendUserMessage(String content){
        String userIDStr = StorageManager.getData("user_id", "");
        Long userID = Long.parseLong(userIDStr);
        UserChatResponseDTO chat = userChat.getValue();
        ChatMessageRequestDTO dto = new ChatMessageRequestDTO(LocalDateTime.now().toString(), false, content);
        SocketProvider.getInstance().getClient()
                .send("/socket-subscriber/user-send-message", gson.toJson(dto))
                .subscribe();
        ChatMessageResponseDTO localUpdate = new ChatMessageResponseDTO(
                userID, chat.getUserName(), content, LocalDateTime.now().toString(), false, chat.getProfilePicture());
        incomingMessage.postValue(localUpdate);
    }

    public void userSubscribeToMessages(Long userID){
        if (messageSubscription != null && !messageSubscription.isDisposed()) return;

        messageSubscription = SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/admin-messages/" + userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    ChatMessageResponseDTO response = gson.fromJson(message.getPayload(), ChatMessageResponseDTO.class);
                    incomingMessage.postValue(response);
                }, throwable -> Log.e("STOMP", "Subscription Error", throwable));
    }

    public void adminSubscribeToMessages(){
        if (messageSubscription != null && !messageSubscription.isDisposed()) return;

        messageSubscription = SocketProvider.getInstance().getClient()
                .topic("/socket-publisher/user-messages")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    ChatMessageResponseDTO response = gson.fromJson(message.getPayload(), ChatMessageResponseDTO.class);
                    incomingMessage.postValue(response);
                }, throwable -> Log.e("STOMP", "Admin Sub Error", throwable));
    }

    @Override
    protected void onCleared() {
        if (messageSubscription != null) messageSubscription.dispose();
        super.onCleared();
    }
}
