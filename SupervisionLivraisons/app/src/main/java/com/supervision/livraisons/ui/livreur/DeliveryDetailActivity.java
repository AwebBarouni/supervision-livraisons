package com.supervision.livraisons.ui.livreur;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.databinding.ActivityDeliveryDetailBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.StatusColorUtil;
import com.supervision.livraisons.viewmodel.DeliveryViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeliveryDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDeliveryDetailBinding binding;
    private DeliveryViewModel viewModel;

    private String deliveryId;
    private GoogleMap googleMap;
    private Delivery currentDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliveryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);

        deliveryId = getIntent().getStringExtra(Constants.EXTRA_DELIVERY_ID);
        if (TextUtils.isEmpty(deliveryId)) {
            finish();
            return;
        }

        setupToolbar();
        setupMap();
        setupActions();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadDelivery(deliveryId);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapContainer);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupActions() {
        binding.btnUpdateStatus.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateDeliveryStatusActivity.class);
            intent.putExtra(Constants.EXTRA_DELIVERY_ID, deliveryId);
            startActivity(intent);
        });

        binding.tvPhoneValue.setOnClickListener(v -> {
            String phone = binding.tvPhoneValue.getText().toString().trim();
            if (!phone.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            }
        });

        binding.btnOpenMaps.setOnClickListener(v -> openInMaps());
    }

    private void openInMaps() {
        if (currentDelivery == null) return;
        Uri geoUri;
        if (currentDelivery.getLat() != null && currentDelivery.getLng() != null) {
            geoUri = Uri.parse("geo:" + currentDelivery.getLat() + "," + currentDelivery.getLng()
                    + "?q=" + Uri.encode(safe(currentDelivery.getAddress())));
        } else {
            geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(safe(currentDelivery.getAddress())));
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Uri browserUri = Uri.parse("https://maps.google.com/maps?q="
                    + Uri.encode(safe(currentDelivery.getAddress())));
            startActivity(new Intent(Intent.ACTION_VIEW, browserUri));
        }
    }

    private void observeViewModel() {
        viewModel.getDeliveryDetail().observe(this, delivery -> {
            if (delivery == null) {
                return;
            }
            currentDelivery = delivery;
            bindDelivery(delivery);
            updateMap();
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

    private void bindDelivery(Delivery delivery) {
        binding.tvClientValue.setText(safe(delivery.getClientName()));
        binding.tvPhoneValue.setText(safe(delivery.getClientPhone()));
        binding.tvAddressValue.setText(safe(delivery.getAddress()));
        binding.tvOrderValue.setText(safe(delivery.getOrderDetails()));
        binding.tvNotesValue.setText(safe(delivery.getNotes()));

        binding.tvStatusBadge.setText(StatusColorUtil.getLabel(this, delivery.getStatus()));
        int statusColor = StatusColorUtil.getColor(this, delivery.getStatus());
        binding.tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(statusColor));
        binding.tvStatusBadge.setTextColor(StatusColorUtil.getBadgeTextColor(delivery.getStatus()));
    }

    private void updateMap() {
        if (googleMap == null || currentDelivery == null || currentDelivery.getLat() == null || currentDelivery.getLng() == null) {
            return;
        }

        LatLng location = new LatLng(currentDelivery.getLat(), currentDelivery.getLng());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(location).title(currentDelivery.getClientName()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        updateMap();
    }
}
