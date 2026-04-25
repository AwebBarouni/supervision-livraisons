package com.supervision.livraisons.ui.livreur;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.supervision.livraisons.R;
import com.supervision.livraisons.adapter.DeliveryAdapter;
import com.supervision.livraisons.databinding.ActivityAllDeliveriesBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.ui.auth.LoginActivity;
import com.supervision.livraisons.ui.messaging.MessagingScreenActivity;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.DeliveryViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AllDeliveriesActivity extends AppCompatActivity {

    private static final int MENU_SEARCH_ID = 1001;

    private ActivityAllDeliveriesBinding binding;
    private DeliveryViewModel viewModel;
    private DeliveryAdapter adapter;

    private final List<Delivery> allDeliveries = new ArrayList<>();
    private String selectedStatus = "ALL";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllDeliveriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        setupToolbar();
        setupTabs();
        setupRecycler();
        setupBottomNavigation();
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

        Menu menu = binding.toolbar.getMenu();
        MenuItem searchItem = menu.add(Menu.NONE, MENU_SEARCH_ID, Menu.NONE, getString(R.string.action_search));
        searchItem.setIcon(R.drawable.ic_search);
        searchItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        SearchView searchView = new SearchView(this);
        searchView.setQueryHint(getString(R.string.hint_search));
        searchItem.setActionView(searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query == null ? "" : query.trim();
                applyFilters();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText == null ? "" : newText.trim();
                applyFilters();
                return true;
            }
        });

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                performLogout();
                return true;
            }
            return false;
        });
    }

    private void setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_tous));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_en_attente));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_en_cours));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_livre));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.tab_echoue));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == null) {
                    return;
                }
                switch (tab.getPosition()) {
                    case 1:
                        selectedStatus = Constants.STATUS_EN_ATTENTE;
                        break;
                    case 2:
                        selectedStatus = Constants.STATUS_EN_COURS;
                        break;
                    case 3:
                        selectedStatus = Constants.STATUS_LIVRE;
                        break;
                    case 4:
                        selectedStatus = Constants.STATUS_ECHOUE;
                        break;
                    default:
                        selectedStatus = "ALL";
                        break;
                }
                applyFilters();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
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

    private void setupBottomNavigation() {
        binding.bottomNav.setSelectedItemId(R.id.nav_livraisons);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_accueil) {
                startActivity(new Intent(this, TodaysDeliveriesActivity.class));
                finish();
                return true;
            }
            if (id == R.id.nav_livraisons) {
                return true;
            }
            if (id == R.id.nav_messages) {
                startActivity(new Intent(this, MessagingScreenActivity.class));
                return true;
            }
            if (id == R.id.nav_profil) {
                showProfileDialog();
                binding.bottomNav.setSelectedItemId(R.id.nav_livraisons);
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getDeliveries().observe(this, deliveries -> {
            allDeliveries.clear();
            if (deliveries != null) {
                allDeliveries.addAll(deliveries);
            }
            applyFilters();
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

    private void applyFilters() {
        List<Delivery> filtered = new ArrayList<>();
        String normalizedQuery = searchQuery.toLowerCase(Locale.getDefault());

        for (Delivery delivery : allDeliveries) {
            boolean statusMatches = "ALL".equals(selectedStatus) || selectedStatus.equals(delivery.getStatus());

            String name = delivery.getClientName() == null ? "" : delivery.getClientName().toLowerCase(Locale.getDefault());
            String address = delivery.getAddress() == null ? "" : delivery.getAddress().toLowerCase(Locale.getDefault());
            boolean queryMatches = normalizedQuery.isEmpty() || name.contains(normalizedQuery) || address.contains(normalizedQuery);

            if (statusMatches && queryMatches) {
                filtered.add(delivery);
            }
        }

        adapter.setFilter(filtered);
        binding.tvEmptyState.setVisibility(filtered.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
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
