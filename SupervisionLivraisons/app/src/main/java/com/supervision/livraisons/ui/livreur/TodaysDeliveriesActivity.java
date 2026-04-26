package com.supervision.livraisons.ui.livreur;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.adapter.DeliveryAdapter;
import com.supervision.livraisons.databinding.ActivityTodaysDeliveriesBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.ui.auth.LoginActivity;
import com.supervision.livraisons.ui.messaging.MessagingScreenActivity;
import com.supervision.livraisons.ui.profile.UserProfileActivity;
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
    private FusedLocationProviderClient fusedLocationClient;
    private Double currentLat = null;
    private Double currentLng = null;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    loadDeliveriesWithLocation();
                } else {
                    viewModel.loadTodayDeliveries(this, null, null);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTodaysDeliveriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupToolbar();
        setupRecycler();
        setupBottomNavigation();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationAndSync();
    }

    private void requestLocationAndSync() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            loadDeliveriesWithLocation();
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void loadDeliveriesWithLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLat = location.getLatitude();
                    currentLng = location.getLongitude();
                }
                viewModel.loadTodayDeliveries(this, currentLat, currentLng);
            }).addOnFailureListener(e -> viewModel.loadTodayDeliveries(this, null, null));
        } catch (SecurityException e) {
            viewModel.loadTodayDeliveries(this, null, null);
        }
    }

    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
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

    private boolean isDone(String status) {
        return Constants.STATUS_LIVRE.equals(status) || Constants.STATUS_ECHOUE.equals(status);
    }

    private double distanceFromMe(com.supervision.livraisons.model.Delivery d) {
        if (currentLat == null || currentLng == null || d.getLat() == null || d.getLng() == null) {
            return Double.MAX_VALUE;
        }
        return haversineKm(currentLat, currentLng, d.getLat(), d.getLng());
    }

    private void sortAndDisplay(List<Delivery> deliveries) {
        deliveries.sort((a, b) -> {
            boolean aDone = isDone(a.getStatus());
            boolean bDone = isDone(b.getStatus());
            if (aDone != bDone) return aDone ? 1 : -1;
            return Double.compare(distanceFromMe(a), distanceFromMe(b));
        });
        adapter.setFilter(deliveries);
        binding.tvEmptyState.setVisibility(deliveries.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        updateSummary(deliveries);
    }

    private void observeViewModel() {
        viewModel.getDeliveries().observe(this, deliveries -> {
            List<Delivery> safe = deliveries != null ? new ArrayList<>(deliveries) : new ArrayList<>();
            sortAndDisplay(safe);
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
                startActivity(new Intent(this, UserProfileActivity.class));
                binding.bottomNav.setSelectedItemId(R.id.nav_accueil);
                return true;
            }
            return false;
        });
    }

    private void performLogout() {
        SessionManager.clearSession(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
