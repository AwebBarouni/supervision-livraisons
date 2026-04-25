package com.supervision.livraisons.ui.messaging;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.adapter.ConversationAdapter;
import com.supervision.livraisons.databinding.ActivityMessagingScreenBinding;
import com.supervision.livraisons.model.Conversation;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.ui.auth.LoginActivity;
import com.supervision.livraisons.ui.controleur.ControleurDashboardActivity;
import com.supervision.livraisons.ui.livreur.AllDeliveriesActivity;
import com.supervision.livraisons.ui.livreur.TodaysDeliveriesActivity;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.DashboardViewModel;
import com.supervision.livraisons.viewmodel.MessageViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessagingScreenActivity extends AppCompatActivity {

    private ActivityMessagingScreenBinding binding;
    private MessageViewModel messageViewModel;
    private DashboardViewModel dashboardViewModel;
    private ConversationAdapter adapter;

    private final List<Conversation> currentConversations = new ArrayList<>();
    private final List<User> livreurs = new ArrayList<>();

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagingScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        role = SessionManager.getUserRole(this);
        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupRecycler();
        setupBottomNavigation();
        setupFab();
        observeViewModels();
    }

    @Override
    protected void onResume() {
        super.onResume();
        messageViewModel.loadConversations(this);
        if (Constants.ROLE_CONTROLEUR.equals(role)) {
            dashboardViewModel.loadLivreurs(this);
        }
    }

    private void setupRecycler() {
        adapter = new ConversationAdapter(new ArrayList<>(), conversation -> openConversation(
                conversation.getPartnerId(),
                conversation.getPartnerName()
        ));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupFab() {
        binding.fabNewConversation.setOnClickListener(v -> showNewConversationDialog());
    }

    private void observeViewModels() {
        messageViewModel.getConversations().observe(this, conversations -> {
            currentConversations.clear();
            if (conversations != null) {
                currentConversations.addAll(conversations);
            }
            adapter.submitList(currentConversations);
            binding.tvEmptyState.setVisibility(currentConversations.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        messageViewModel.getIsLoading().observe(this, loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            binding.progressBar.setVisibility(isLoading ? android.view.View.VISIBLE : android.view.View.GONE);
        });

        messageViewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });

        dashboardViewModel.getLivreurs().observe(this, users -> {
            livreurs.clear();
            if (users != null) {
                livreurs.addAll(users);
            }
        });

        dashboardViewModel.getErrorMessage().observe(this, message -> {
            if (message != null && Constants.ROLE_CONTROLEUR.equals(role)) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNav.getMenu().clear();
        if (Constants.ROLE_CONTROLEUR.equals(role)) {
            binding.bottomNav.inflateMenu(R.menu.bottom_nav_controleur);
            binding.bottomNav.setSelectedItemId(R.id.nav_messages);
        } else {
            binding.bottomNav.inflateMenu(R.menu.bottom_nav_livreur);
            binding.bottomNav.setSelectedItemId(R.id.nav_messages);
        }

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (Constants.ROLE_CONTROLEUR.equals(role)) {
                if (id == R.id.nav_dashboard) {
                    startActivity(new Intent(this, ControleurDashboardActivity.class));
                    finish();
                    return true;
                }
                if (id == R.id.nav_livraisons) {
                    startActivity(new Intent(this, AllDeliveriesActivity.class));
                    return true;
                }
                if (id == R.id.nav_livreurs) {
                    Snackbar.make(binding.getRoot(), getString(R.string.label_livreurs_actifs), Snackbar.LENGTH_SHORT).show();
                    return true;
                }
                return id == R.id.nav_messages;
            }

            if (id == R.id.nav_accueil) {
                startActivity(new Intent(this, TodaysDeliveriesActivity.class));
                finish();
                return true;
            }
            if (id == R.id.nav_livraisons) {
                startActivity(new Intent(this, AllDeliveriesActivity.class));
                return true;
            }
            if (id == R.id.nav_messages) {
                return true;
            }
            if (id == R.id.nav_profil) {
                showProfileDialog();
                binding.bottomNav.setSelectedItemId(R.id.nav_messages);
                return true;
            }
            return false;
        });
    }

    private void showNewConversationDialog() {
        if (Constants.ROLE_CONTROLEUR.equals(role) && !livreurs.isEmpty()) {
            String[] names = new String[livreurs.size()];
            for (int i = 0; i < livreurs.size(); i++) {
                names[i] = livreurs.get(i).getName();
            }

            new AlertDialog.Builder(this)
                    .setTitle(R.string.select_receiver)
                    .setItems(names, (dialog, which) -> {
                        User selected = livreurs.get(which);
                        openConversation(selected.getId(), selected.getName());
                    })
                    .show();
            return;
        }

        if (!currentConversations.isEmpty()) {
            String[] names = new String[currentConversations.size()];
            for (int i = 0; i < currentConversations.size(); i++) {
                names[i] = currentConversations.get(i).getPartnerName();
            }

            new AlertDialog.Builder(this)
                    .setTitle(R.string.select_receiver)
                    .setItems(names, (dialog, which) -> {
                        Conversation selected = currentConversations.get(which);
                        openConversation(selected.getPartnerId(), selected.getPartnerName());
                    })
                    .show();
            return;
        }

        Snackbar.make(binding.getRoot(), getString(R.string.empty_messages), Snackbar.LENGTH_LONG).show();
    }

    private void openConversation(String userId, String userName) {
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        intent.putExtra(Constants.EXTRA_USER_NAME, userName);
        startActivity(intent);
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
