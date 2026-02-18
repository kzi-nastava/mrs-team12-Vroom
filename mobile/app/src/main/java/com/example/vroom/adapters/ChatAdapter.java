package com.example.vroom.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vroom.DTOs.chat.response.ChatMessageResponseDTO;
import com.example.vroom.R;
import com.google.android.material.imageview.ShapeableImageView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;
    private List<ChatMessageResponseDTO> messages;
    private final String currentUserRole;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ChatAdapter(List<ChatMessageResponseDTO> messages, String currentUserRole) {
        this.messages = messages;
        this.currentUserRole = currentUserRole;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageResponseDTO message = messages.get(position);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUserRole);

        if (isAdmin) {
            return message.isSentByAdmin() ? TYPE_SENT : TYPE_RECEIVED;
        } else {
            return message.isSentByAdmin() ? TYPE_RECEIVED : TYPE_SENT;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.bind(messages.get(position), getItemViewType(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<ChatMessageResponseDTO> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessageResponseDTO message) {
        this.messages.add(message);
        notifyItemInserted(this.messages.size() - 1);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView content, meta;
        ShapeableImageView profileImage;
        LinearLayout bubbleContainer;

        MessageViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.bubbleContent);
            meta = itemView.findViewById(R.id.textMeta);
            profileImage = itemView.findViewById(R.id.imageProfile);
            bubbleContainer = itemView.findViewById(R.id.bubbleContainer);
        }

        void bind(ChatMessageResponseDTO msg, int type) {
            content.setText(msg.getContent());
            String time = LocalDateTime.parse(msg.getTimestamp()).format(timeFormatter);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bubbleContainer.getLayoutParams();

            if (type == TYPE_SENT) {
                content.setBackgroundResource(R.drawable.bg_bubble_sent);
                profileImage.setVisibility(View.GONE);
                params.startToStart = ConstraintLayout.LayoutParams.UNSET;
                params.startToEnd = ConstraintLayout.LayoutParams.UNSET;
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                bubbleContainer.setGravity(Gravity.END);
                meta.setText(time);
            } else {
                content.setBackgroundResource(R.drawable.bg_bubble_received);
                profileImage.setVisibility(View.VISIBLE);
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET;
                params.startToStart = ConstraintLayout.LayoutParams.UNSET;
                params.startToEnd = R.id.imageProfile;
                bubbleContainer.setGravity(Gravity.START);
                meta.setText(msg.getSenderName() + " • " + time);
                setProfilePicture(msg.getProfilePicture());
            }

            bubbleContainer.setLayoutParams(params);
        }

        private void setProfilePicture(String base64) {
            if (base64 != null && !base64.isEmpty()) {
                try {
                    byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profileImage.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    profileImage.setImageResource(R.drawable.ic_user);
                }
            } else {
                profileImage.setImageResource(R.drawable.ic_user);
            }
        }
    }
}