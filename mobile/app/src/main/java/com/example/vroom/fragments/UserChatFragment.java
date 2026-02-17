package com.example.vroom.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vroom.DTOs.chat.response.ChatMessageResponseDTO;
import com.example.vroom.R;
import com.example.vroom.adapters.ChatAdapter;
import com.example.vroom.data.local.StorageManager;
import com.example.vroom.viewmodels.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserChatFragment extends Fragment {
    private ChatViewModel chatViewModel;
    private ChatAdapter chatAdapter;
    private EditText editTextMessage;
    private RecyclerView recyclerView;
    private long targetChatId = -1L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_chat, container, false);

        chatViewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        Button buttonSend = view.findViewById(R.id.buttonSend);
        recyclerView = view.findViewById(R.id.recyclerViewMessages);
        setupRecyclerView(new ArrayList<>());

        if (getArguments() != null) {
            targetChatId = getArguments().getLong("chatId", -1L);
        }

        if (targetChatId != -1L) {
            chatViewModel.loadAdminChat(targetChatId);
        } else {
            chatViewModel.loadUserChat();
        }

        observeMessages();
        buttonSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void setupRecyclerView(List<ChatMessageResponseDTO> messages) {
        String role = StorageManager.getData("user_type", "");
        chatAdapter = new ChatAdapter(messages, role);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);
    }

    private void observeMessages() {
        chatViewModel.getAllMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null && chatAdapter != null) {
                chatAdapter.setMessages(messages);
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }
        });

        chatViewModel.getIncomingMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && chatAdapter != null) {
                if (targetChatId == -1L || targetChatId == message.getChatID()){
                    chatAdapter.addMessage(message);
                    recyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });
    }

    private void sendMessage() {
        String content = editTextMessage.getText().toString().trim();
        if (!content.isEmpty()) {
            if (targetChatId != -1L) {
                chatViewModel.sendAdminMessage(targetChatId, content);
            } else {
                chatViewModel.sendUserMessage(content);
            }
            editTextMessage.setText("");
        }
    }
}