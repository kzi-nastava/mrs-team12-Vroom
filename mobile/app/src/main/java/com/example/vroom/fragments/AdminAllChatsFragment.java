package com.example.vroom.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.chat.response.ChatResponseDTO;
import com.example.vroom.R;
import com.example.vroom.adapters.AllChatsAdapter;
import com.example.vroom.viewmodels.ChatViewModel;

public class AdminAllChatsFragment extends Fragment {
    private ChatViewModel viewModel;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_all_chats, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewAllChats);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.getAllChats().observe(getViewLifecycleOwner(), chats -> {
            recyclerView.setAdapter(new AllChatsAdapter(chats, chat -> {
                Bundle bundle = new Bundle();
                bundle.putLong("chatId", chat.getChatId());

                UserChatFragment chatFragment = new UserChatFragment();
                chatFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, chatFragment)
                        .addToBackStack(null)
                        .commit();
            }));
        });

        viewModel.loadAllChats();
        viewModel.adminSubscribeToMessages();

        return view;
    }
}