package com.supervision.livraisons.ui.livreur;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.adapter.DeliveryAdapter;
import com.supervision.livraisons.databinding.ActivityTodaysDeliveriesBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.ui.auth.LoginActivity;
import com.supervision.livraisons.ui.messaging.MessagingScreenActivity;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.DeliveryViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TodaysDeliveriesActivity extends AppCompatActivity {

    private ActivityTodaysDeliveriesBinding binding;
    private DeliveryViewModel viewModel;
    private DeliveryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTodaysDeliveriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        setupToolbar();
        setupRecycler();
        setupBottomNavigation();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadTodayDeliveries(this);
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                performLogout();
                return true;
            }
            return false;
        });
    }

    private void setupRecycler() {
        adapter = new DeliveryAdapter(new ArrayList<>(), delivery -> {
            Intent intent = new Intent(this, DeliveryDetailActivity.class);
            intent.putExtra(Constants.EXTRA_DELIVERY_ID, delivery.getId());
            startActivity(intent);
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getDeliveries().observe(this, deliveries -> {
            List<Delivery> safe = deliveries != null ? deliveries : new ArrayList<>();
            adapter.setFilter(safe);
            binding.tvEmptyState.setVisibility(safe.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
            updateSummary(safe);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        });
    }

    private void updateSummary(List<Delivery> deliveries) {
        int total = deliveries.size();
        int delivered = 0;
        int inProgress = 0;

        for (Delivery delivery : deliveries) {
            if (Constants.STATUS_LIVRE.equals(delivery.getStatus())) {
                delivered++;
            }
            if (Constants.STATUS_EN_COURS.equals(delivery.getStatus())) {
                inProgress++;
            }
        }

        binding.tvTotalValue.setText(String.valueOf(total));
        binding.tvDeliveredValue.setText(String.valueOf(delivered));
        binding.tvInProgressValue.setText(String.valueOf(inProgress));
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setSelectedItemId(R.id.nav_accueil);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_accueil) {
                return true;
            }
            if (id == R.id.nav_livraisons) {
                startActivity(new Intent(this, AllDeliveriesActivity.class));
                return true;
            }
            if (id == R.id.nav_messages) {
                startActivity(new Intent(this, MessagingScreenActivity.class));
                return true;
            }
            if (id == R.id.nav_profil) {
                showProfileDialog();
                binding.bottomNav.setSelectedItemId(R.id.nav_accueil);
                return true;
            }
            return false;
        });
    }

    private void showProfileDialog() {
        String userName = SessionManager.getUserName(this);
        String profileText = (userName == null ? "" : userName) + "\n\n" + getString(R.string.logout_question);

        new AlertDialog.Builder(this)
                .setTitle(R.string.title_profile)
                .setMessage(profileText)
                .setPositiveButton(R.string.btn_logout, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void performLogout() {
        SessionManager.clearSession(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
