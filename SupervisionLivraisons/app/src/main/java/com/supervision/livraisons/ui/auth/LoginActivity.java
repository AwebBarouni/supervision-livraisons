package com.supervision.livraisons.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.databinding.ActivityLoginBinding;
import com.supervision.livraisons.ui.controleur.ControleurDashboardActivity;
import com.supervision.livraisons.ui.livreur.TodaysDeliveriesActivity;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.AuthViewModel;
import com.supervision.livraisons.worker.SyncWorkScheduler;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (SessionManager.isLoggedIn(this)) {
            routeByRole(SessionManager.getUserRole(this));
            finish();
            return;
        }

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        observeViewModel();

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void observeViewModel() {
        authViewModel.getLoginResult().observe(this, response -> {
            if (response == null) {
                return;
            }

            SessionManager.saveSession(
                    this,
                    response.getToken(),
                    response.getUserId(),
                    response.getName(),
                    response.getRole());

            routeByRole(response.getRole());
            finish();
        });

        authViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });

        authViewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
        });
    }

    private void attemptLogin() {
        String email = String.valueOf(binding.etEmail.getText()).trim();
        String password = String.valueOf(binding.etPassword.getText()).trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Snackbar.make(binding.getRoot(), getString(R.string.error_empty_fields), Snackbar.LENGTH_LONG).show();
            return;
        }

        authViewModel.login(this, email, password);
    }

    private void routeByRole(String role) {
        if (Constants.ROLE_LIVREUR.equals(role)) {
            SyncWorkScheduler.enqueueDailySync(this, SessionManager.getUserId(this));
        }

        Class<?> destination = Constants.ROLE_CONTROLEUR.equals(role)
                ? ControleurDashboardActivity.class
                : TodaysDeliveriesActivity.class;

        Intent intent = new Intent(this, destination);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
