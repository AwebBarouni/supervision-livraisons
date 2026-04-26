package com.supervision.livraisons.ui.profile;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.databinding.ActivityUserProfileBinding;
import com.supervision.livraisons.dto.UserRequest;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.UserViewModel;

import java.util.Locale;

public class UserProfileActivity extends AppCompatActivity {

    public static final String EXTRA_IS_NEW_USER = "is_new_user";

    private ActivityUserProfileBinding binding;
    private UserViewModel viewModel;

    private String targetUserId;
    private boolean isNewUser;
    private boolean isController;
    private boolean isSelf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(UserViewModel.class);

        targetUserId = getIntent().getStringExtra(Constants.EXTRA_USER_ID);
        isNewUser    = getIntent().getBooleanExtra(EXTRA_IS_NEW_USER, false);
        isController = Constants.ROLE_CONTROLEUR.equals(SessionManager.getUserRole(this));
        isSelf       = !isNewUser && TextUtils.isEmpty(targetUserId);

        setupToolbar();
        setupRoleVisibility();
        observeViewModel();

        if (isNewUser) {
            binding.toolbar.setTitle(getString(R.string.title_new_user));
            binding.tvAvatar.setText("+");
            binding.tvAvatar.setBackground(circleDrawable(ContextCompat.getColor(this, R.color.colorPrimary)));
            binding.tvUserRole.setVisibility(View.GONE);
            binding.rgRole.check(R.id.rbLivreur);
        } else if (!TextUtils.isEmpty(targetUserId)) {
            viewModel.loadUser(this, targetUserId);
        } else {
            viewModel.loadMe(this);
        }

        binding.btnSave.setOnClickListener(v -> onSave());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRoleVisibility() {
        if (isController && (isNewUser || !isSelf)) {
            binding.layoutRole.setVisibility(View.VISIBLE);
        }
    }

    private void observeViewModel() {
        viewModel.getUser().observe(this, this::bindUser);

        viewModel.getIsLoading().observe(this, loading -> {
            binding.progressBar.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!Boolean.TRUE.equals(loading));
        });

        viewModel.getSaveSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Snackbar.make(binding.getRoot(), getString(R.string.profile_saved), Snackbar.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void bindUser(User user) {
        if (user == null) return;

        binding.etName.setText(user.getName());
        binding.etEmail.setText(user.getEmail());

        String letter = firstLetter(user.getName());
        binding.tvAvatar.setText(letter);
        int color = Constants.ROLE_CONTROLEUR.equals(user.getRole())
                ? ContextCompat.getColor(this, R.color.colorPrimary)
                : ContextCompat.getColor(this, R.color.colorSuccess);
        binding.tvAvatar.setBackground(circleDrawable(color));

        String roleLabel = Constants.ROLE_CONTROLEUR.equals(user.getRole())
                ? getString(R.string.role_controleur)
                : getString(R.string.role_livreur);
        binding.tvUserRole.setText(roleLabel);
        binding.tvUserRole.setVisibility(View.VISIBLE);
        binding.tvUserRole.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Constants.ROLE_CONTROLEUR.equals(user.getRole())
                                ? ContextCompat.getColor(this, R.color.colorPrimary)
                                : ContextCompat.getColor(this, R.color.colorSuccess)));

        if (isController && !isSelf) {
            if (Constants.ROLE_CONTROLEUR.equals(user.getRole())) {
                binding.rgRole.check(R.id.rbControleur);
            } else {
                binding.rgRole.check(R.id.rbLivreur);
            }
        }
    }

    private void onSave() {
        String name  = text(binding.etName);
        String email = text(binding.etEmail);
        String pwd   = text(binding.etPassword);

        if (isNewUser && (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd))) {
            Snackbar.make(binding.getRoot(), getString(R.string.error_empty_fields), Snackbar.LENGTH_SHORT).show();
            return;
        }

        UserRequest request = new UserRequest();
        if (!TextUtils.isEmpty(name))  request.setName(name);
        if (!TextUtils.isEmpty(email)) request.setEmail(email);
        if (!TextUtils.isEmpty(pwd))   request.setPassword(pwd);

        if (isController && binding.layoutRole.getVisibility() == View.VISIBLE) {
            String role = binding.rgRole.getCheckedRadioButtonId() == R.id.rbControleur
                    ? Constants.ROLE_CONTROLEUR
                    : Constants.ROLE_LIVREUR;
            request.setRole(role);
        }

        if (isNewUser) {
            viewModel.createUser(this, request);
        } else {
            String id = !TextUtils.isEmpty(targetUserId)
                    ? targetUserId
                    : viewModel.getUser().getValue() != null ? viewModel.getUser().getValue().getId() : null;
            if (id == null) return;
            viewModel.updateUser(this, id, request);
        }
    }

    private String text(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private String firstLetter(String text) {
        if (text == null || text.trim().isEmpty()) return "?";
        return text.trim().substring(0, 1).toUpperCase(Locale.getDefault());
    }

    private GradientDrawable circleDrawable(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
        return d;
    }
}
