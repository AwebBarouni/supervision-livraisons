package com.supervision.livraisons.ui.livreur;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.adapter.DeliveryAdapter;
import com.supervision.livraisons.databinding.ActivitySearchScreenBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.viewmodel.DeliveryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchScreenActivity extends AppCompatActivity {

    private ActivitySearchScreenBinding binding;
    private DeliveryViewModel viewModel;
    private DeliveryAdapter adapter;

    private final List<Delivery> allDeliveries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        setupToolbar();
        setupRecycler();
        setupSearch();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadAllDeliveries(this);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
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

    private void setupSearch() {
        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.clearFocus();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String normalized = query == null ? "" : query.trim();
                if (normalized.isEmpty()) {
                    applyFilter("");
                } else {
                    viewModel.emergencySearch(normalized);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String normalized = newText == null ? "" : newText.trim();
                if (normalized.isEmpty()) {
                    applyFilter("");
                } else {
                    viewModel.emergencySearch(normalized);
                }
                return true;
            }
        });
    }

    private void observeViewModel() {
        viewModel.getDeliveries().observe(this, deliveries -> {
            allDeliveries.clear();
            if (deliveries != null) {
                allDeliveries.addAll(deliveries);
            }
            applyFilter(String.valueOf(binding.searchView.getQuery()));
        });

        viewModel.getEmergencyResults().observe(this, results -> {
            String currentQuery = String.valueOf(binding.searchView.getQuery()).trim();
            if (!currentQuery.isEmpty()) {
                adapter.setFilter(results == null ? new ArrayList<>() : results);
                binding.tvEmptyState.setVisibility(results == null || results.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
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

    private void applyFilter(String query) {
        String normalized = query.toLowerCase(Locale.getDefault()).trim();
        if (!normalized.isEmpty()) {
            return;
        }

        List<Delivery> filtered = new ArrayList<>();

        for (Delivery delivery : allDeliveries) {
            String client = delivery.getClientName() == null ? "" : delivery.getClientName().toLowerCase(Locale.getDefault());
            String address = delivery.getAddress() == null ? "" : delivery.getAddress().toLowerCase(Locale.getDefault());
            if (normalized.isEmpty() || client.contains(normalized) || address.contains(normalized)) {
                filtered.add(delivery);
            }
        }

        adapter.setFilter(filtered);
        binding.tvEmptyState.setVisibility(filtered.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}
