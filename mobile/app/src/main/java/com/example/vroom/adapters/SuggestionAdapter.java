package com.example.vroom.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vroom.DTOs.geocode.responses.AddressSuggestionResponseDTO;
import com.example.vroom.R;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {
    private final List<AddressSuggestionResponseDTO> suggestions;
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(AddressSuggestionResponseDTO selectedDto);
    }
    public SuggestionAdapter(List<AddressSuggestionResponseDTO> suggestions, OnItemClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressSuggestionResponseDTO item = suggestions.get(position);
        holder.addressText.setText(item.getLabel());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions != null ? suggestions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressText = itemView.findViewById(R.id.text_suggestion);
        }
    }
}
