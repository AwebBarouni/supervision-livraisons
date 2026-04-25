package com.supervision.livraisons.adapter;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.supervision.livraisons.databinding.ItemDeliveryBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.util.StatusColorUtil;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {

    public interface OnDeliveryClickListener {
        void onDeliveryClick(Delivery delivery);
    }

    private final List<Delivery> deliveries = new ArrayList<>();
    private final OnDeliveryClickListener listener;

    public DeliveryAdapter(List<Delivery> initialData, OnDeliveryClickListener listener) {
        if (initialData != null) {
            deliveries.addAll(initialData);
        }
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeliveryBinding binding = ItemDeliveryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DeliveryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        holder.bind(deliveries.get(position));
    }

    @Override
    public int getItemCount() {
        return deliveries.size();
    }

    public void setFilter(List<Delivery> filtered) {
        deliveries.clear();
        if (filtered != null) {
            deliveries.addAll(filtered);
        }
        notifyDataSetChanged();
    }

    class DeliveryViewHolder extends RecyclerView.ViewHolder {

        private final ItemDeliveryBinding binding;

        DeliveryViewHolder(ItemDeliveryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Delivery delivery) {
            binding.tvClientName.setText(delivery.getClientName());
            binding.tvAddress.setText(delivery.getAddress());
            binding.tvScheduledTime.setText(formatDate(delivery.getScheduledTime()));
            binding.tvStatus.setText(StatusColorUtil.getLabel(binding.getRoot().getContext(), delivery.getStatus()));

            int statusColor = StatusColorUtil.getColor(binding.getRoot().getContext(), delivery.getStatus());
            binding.viewStatusBar.setBackgroundTintList(ColorStateList.valueOf(statusColor));
            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(statusColor));
            binding.tvStatus.setTextColor(StatusColorUtil.getBadgeTextColor(delivery.getStatus()));

            binding.getRoot().setOnClickListener(v -> listener.onDeliveryClick(delivery));
        }

        private String formatDate(String isoDate) {
            if (isoDate == null || isoDate.isEmpty()) {
                return "";
            }
            try {
                OffsetDateTime parsed = OffsetDateTime.parse(isoDate);
                return parsed.format(DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale.getDefault()));
            } catch (Exception ignored) {
                return isoDate;
            }
        }
    }
}
