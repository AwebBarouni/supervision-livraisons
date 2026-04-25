package com.supervision.livraisons.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supervision.livraisons.databinding.ItemConversationBinding;
import com.supervision.livraisons.model.Conversation;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> conversations = new ArrayList<>();
    private final OnConversationClickListener listener;

    public ConversationAdapter(List<Conversation> initialData, OnConversationClickListener listener) {
        if (initialData != null) {
            conversations.addAll(initialData);
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemConversationBinding binding = ItemConversationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ConversationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.bind(conversations.get(position));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void submitList(List<Conversation> data) {
        conversations.clear();
        if (data != null) {
            conversations.addAll(data);
        }
        notifyDataSetChanged();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        private final ItemConversationBinding binding;

        ConversationViewHolder(ItemConversationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Conversation conversation) {
            String partnerName = conversation.getPartnerName() == null ? "" : conversation.getPartnerName();
            binding.tvPartnerName.setText(partnerName);
            binding.tvLastMessage.setText(conversation.getLastMessage());
            binding.tvTimestamp.setText(formatTimestamp(conversation.getLastTimestamp()));

            if (!partnerName.isEmpty()) {
                binding.tvAvatarInitial.setText(partnerName.substring(0, 1).toUpperCase(Locale.getDefault()));
            } else {
                binding.tvAvatarInitial.setText("?");
            }

            int unread = conversation.getUnreadCount();
            if (unread > 0) {
                binding.tvUnreadBadge.setVisibility(View.VISIBLE);
                binding.tvUnreadBadge.setText(String.valueOf(unread));
            } else {
                binding.tvUnreadBadge.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> listener.onConversationClick(conversation));
        }

        private String formatTimestamp(String isoDate) {
            if (isoDate == null || isoDate.isEmpty()) {
                return "";
            }
            try {
                OffsetDateTime parsed = OffsetDateTime.parse(isoDate);
                return parsed.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()));
            } catch (Exception ignored) {
                return isoDate;
            }
        }
    }
}
