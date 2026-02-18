package com.example.vroom.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vroom.DTOs.chat.response.ChatResponseDTO;
import com.example.vroom.R;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AllChatsAdapter extends RecyclerView.Adapter<AllChatsAdapter.ChatViewHolder> {
    private final List<ChatResponseDTO> chatList;
    private final OnChatClickListener listener;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    public interface OnChatClickListener {
        void onChatClick(ChatResponseDTO chat);
    }

    public AllChatsAdapter(List<ChatResponseDTO> chatList, OnChatClickListener listener) {
        this.chatList = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatResponseDTO chat = chatList.get(position);

        android.util.Log.d("CHAT_DEBUG", "User: " + chat.getUserName());
        android.util.Log.d("CHAT_DEBUG", "PFP String: " + (chat.getProfilePicture() != null ? "Populated" : "NULL"));

        if (chat.getProfilePicture() != null && !chat.getProfilePicture().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(chat.getProfilePicture(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte != null) {
                    holder.profilePicture.setImageBitmap(decodedByte);
                } else {
                    android.util.Log.e("CHAT_DEBUG", "Bitmap decoding failed for " + chat.getUserName());
                    holder.profilePicture.setImageResource(R.drawable.ic_user);
                }
            } catch (Exception e) {
                android.util.Log.e("CHAT_DEBUG", "Base64 error: " + e.getMessage());
                holder.profilePicture.setImageResource(R.drawable.ic_user);
            }
        } else {
            holder.profilePicture.setImageResource(R.drawable.ic_user);
        }

        holder.userName.setText(chat.getUserName());
        holder.itemView.setOnClickListener(v -> listener.onChatClick(chat));
    }
    @Override
    public int getItemCount() { return chatList.size(); }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName, textTime;
        ImageView profilePicture;

        ChatViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textUserName);
            textTime = itemView.findViewById(R.id.textTime);
            profilePicture = itemView.findViewById(R.id.imgProfile);
        }
    }
}