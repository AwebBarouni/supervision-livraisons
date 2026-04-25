package com.supervision.livraisons.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supervision.livraisons.databinding.ItemMessageReceivedBinding;
import com.supervision.livraisons.databinding.ItemMessageSentBinding;
import com.supervision.livraisons.model.Message;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_RECEIVED = 0;
    public static final int VIEW_TYPE_SENT = 1;

    private final List<Message> messages = new ArrayList<>();
    private final String sessionUserId;

    public MessageAdapter(List<Message> initialData, String sessionUserId) {
        if (initialData != null) {
            messages.addAll(initialData);
        }
        this.sessionUserId = sessionUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return sessionUserId != null && sessionUserId.equals(message.getSenderId())
                ? VIEW_TYPE_SENT
                : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            ItemMessageSentBinding binding = ItemMessageSentBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            );
            return new SentViewHolder(binding);
        }

        ItemMessageReceivedBinding binding = ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ReceivedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof SentViewHolder) {
            ((SentViewHolder) holder).bind(message);
        } else {
            ((ReceivedViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> data) {
        messages.clear();
        if (data != null) {
            messages.addAll(data);
        }
        notifyDataSetChanged();
    }

    static class SentViewHolder extends RecyclerView.ViewHolder {

        private final ItemMessageSentBinding binding;

        SentViewHolder(ItemMessageSentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.tvMessage.setText(message.getContent());
            binding.tvTimestamp.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    static class ReceivedViewHolder extends RecyclerView.ViewHolder {

        private final ItemMessageReceivedBinding binding;

        ReceivedViewHolder(ItemMessageReceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Message message) {
            binding.tvMessage.setText(message.getContent());
            binding.tvTimestamp.setText(formatTimestamp(message.getTimestamp()));
        }
    }

    private static String formatTimestamp(String isoDate) {
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
