package com.supervision.livraisons.ui.messaging;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.adapter.MessageAdapter;
import com.supervision.livraisons.databinding.ActivityConversationBinding;
import com.supervision.livraisons.dto.SendMessageRequest;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private ActivityConversationBinding binding;
    private MessageViewModel viewModel;
    private MessageAdapter adapter;

    private String partnerId;
    private String partnerName;
    private String sessionUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        partnerId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        partnerName = getIntent().getStringExtra(Constants.EXTRA_USER_NAME);
        sessionUserId = SessionManager.getUserId(this);

        if (TextUtils.isEmpty(partnerId)) {
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        setupToolbar();
        setupRecycler();
        setupActions();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadMessages(this, partnerId);
    }

    private void setupToolbar() {
        binding.toolbar.setTitle(TextUtils.isEmpty(partnerName) ? getString(R.string.title_conversation) : partnerName);
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);

        adapter = new MessageAdapter(new ArrayList<>(), sessionUserId);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupActions() {
        binding.btnSend.setOnClickListener(v -> {
            String content = String.valueOf(binding.etMessage.getText()).trim();
            if (content.isEmpty()) {
                Snackbar.make(binding.getRoot(), getString(R.string.message_required), Snackbar.LENGTH_LONG).show();
                return;
            }
            viewModel.sendMessage(this, new SendMessageRequest(partnerId, content, false));
        });
    }

    private void observeViewModel() {
        viewModel.getMessages().observe(this, messages -> {
            List<Message> safe = messages == null ? new ArrayList<>() : messages;
            adapter.setMessages(safe);
            binding.recyclerView.scrollToPosition(Math.max(0, safe.size() - 1));
            markIncomingAsRead(safe);
        });

        viewModel.getSendSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                binding.etMessage.setText("");
                viewModel.loadMessages(this, partnerId);
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void markIncomingAsRead(List<Message> messages) {
        for (Message message : messages) {
            if (message == null) {
                continue;
            }
            if (!message.isRead()
                    && sessionUserId != null
                    && sessionUserId.equals(message.getReceiverId())
                    && !TextUtils.isEmpty(message.getId())) {
                viewModel.markAsRead(this, message.getId());
            }
        }
    }
}
