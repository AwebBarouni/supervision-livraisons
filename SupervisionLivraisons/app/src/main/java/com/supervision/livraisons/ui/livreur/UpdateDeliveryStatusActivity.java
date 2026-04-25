package com.supervision.livraisons.ui.livreur;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.databinding.ActivityUpdateDeliveryStatusBinding;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.viewmodel.DeliveryViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UpdateDeliveryStatusActivity extends AppCompatActivity {

    private ActivityUpdateDeliveryStatusBinding binding;
    private DeliveryViewModel viewModel;
    private String deliveryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateDeliveryStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DeliveryViewModel.class);
        deliveryId = getIntent().getStringExtra(Constants.EXTRA_DELIVERY_ID);

        setupToolbar();
        setupFailureSpinner();
        setupStatusSelector();
        setupObservers();

        binding.btnConfirm.setOnClickListener(v -> submitStatusUpdate());

        if (!TextUtils.isEmpty(deliveryId)) {
            viewModel.loadDelivery(deliveryId);
        }
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupFailureSpinner() {
        List<String> reasons = new ArrayList<>();
        reasons.add(getString(R.string.failure_absent));
        reasons.add(getString(R.string.failure_refus));
        reasons.add(getString(R.string.failure_bad_address));
        reasons.add(getString(R.string.failure_other));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                reasons
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFailureReason.setAdapter(adapter);
    }

    private void setupStatusSelector() {
        binding.rgStatus.check(R.id.rbEnCours);
        toggleFailureViews(false);

        binding.rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
            boolean failedSelected = checkedId == R.id.rbEchoue;
            toggleFailureViews(failedSelected);
        });
    }

    private void setupObservers() {
        viewModel.getDeliveryDetail().observe(this, delivery -> {
            if (delivery != null) {
                binding.tvClientSummary.setText(delivery.getClientName());
                binding.tvAddressSummary.setText(delivery.getAddress());
            }
        });

        viewModel.getUpdateSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Snackbar.make(binding.getRoot(), getString(R.string.status_updated), Snackbar.LENGTH_LONG).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnConfirm.setEnabled(!isLoading);
        });
    }

    private void submitStatusUpdate() {
        if (TextUtils.isEmpty(deliveryId)) {
            return;
        }

        int checkedId = binding.rgStatus.getCheckedRadioButtonId();

        String status;

        if (checkedId == R.id.rbLivre) {
            status = Constants.STATUS_LIVRE;
        } else if (checkedId == R.id.rbEchoue) {
            status = Constants.STATUS_ECHOUE;
        } else {
            status = Constants.STATUS_EN_COURS;
        }

        viewModel.updateStatus(deliveryId, status);
    }

    private void toggleFailureViews(boolean visible) {
        int visibility = visible ? View.VISIBLE : View.GONE;
        binding.tvFailureReasonLabel.setVisibility(visibility);
        binding.spinnerFailureReason.setVisibility(visibility);
    }
}
