package com.supervision.livraisons.ui.controleur;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.databinding.ActivityEmergencyMessageBinding;
import com.supervision.livraisons.dto.SendMessageRequest;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.viewmodel.DashboardViewModel;
import com.supervision.livraisons.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class EmergencyMessageActivity extends AppCompatActivity {

    private ActivityEmergencyMessageBinding binding;
    private DashboardViewModel dashboardViewModel;
    private MessageViewModel messageViewModel;

    private final List<User> livreurs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        setupToolbar();
        observeViewModels();
        setupSendAction();

        dashboardViewModel.loadLivreurs(this);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void observeViewModels() {
        dashboardViewModel.getLivreurs().observe(this, users -> {
            livreurs.clear();
            if (users != null) {
                livreurs.addAll(users);
            }
            populateSpinner();
        });

        dashboardViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });

        messageViewModel.getSendSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Snackbar.make(binding.getRoot(), getString(R.string.message_emergency_sent), Snackbar.LENGTH_LONG).show();
                finish();
            }
        });

        messageViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });

        messageViewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSendEmergency.setEnabled(!isLoading);
        });
    }

    private void setupSendAction() {
        binding.btnSendEmergency.setOnClickListener(v -> {
            String messageText = String.valueOf(binding.etEmergencyMessage.getText()).trim();
            if (TextUtils.isEmpty(messageText)) {
                Snackbar.make(binding.getRoot(), getString(R.string.message_required), Snackbar.LENGTH_LONG).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setMessage(R.string.confirm_emergency)
                    .setPositiveButton(R.string.btn_confirm, (dialog, which) -> sendEmergency(messageText))
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        });
    }

    private void sendEmergency(String content) {
        int selectedIndex = binding.spinnerReceivers.getSelectedItemPosition();
        String receiverId = null;

        if (selectedIndex > 0 && selectedIndex - 1 < livreurs.size()) {
            receiverId = livreurs.get(selectedIndex - 1).getId();
        }

        SendMessageRequest request = new SendMessageRequest(receiverId, content, true);
        messageViewModel.sendMessage(this, request);
    }

    private void populateSpinner() {
        List<String> labels = new ArrayList<>();
        labels.add(getString(R.string.label_all_livreurs));
        for (User user : livreurs) {
            labels.add(user.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerReceivers.setAdapter(adapter);
    }
}
