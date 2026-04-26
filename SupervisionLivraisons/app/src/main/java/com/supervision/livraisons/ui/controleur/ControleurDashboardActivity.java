package com.supervision.livraisons.ui.controleur;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.supervision.livraisons.R;
import com.supervision.livraisons.databinding.ActivityControleurDashboardBinding;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.DeliveryStats;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.ui.auth.LoginActivity;
import com.supervision.livraisons.ui.livreur.AllDeliveriesActivity;
import com.supervision.livraisons.ui.messaging.MessagingScreenActivity;
import com.supervision.livraisons.ui.profile.UserProfileActivity;
import com.supervision.livraisons.util.Constants;
import com.supervision.livraisons.util.SessionManager;
import com.supervision.livraisons.viewmodel.DashboardViewModel;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControleurDashboardActivity extends AppCompatActivity {

    private ActivityControleurDashboardBinding binding;
    private DashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControleurDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        setupToolbar();
        setupBottomNavigation();
        setupEmergencyFab();
        observeViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadStats(this);
        viewModel.loadLivreurs(this);
        viewModel.loadAllDeliveries(this);
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

    private void setupEmergencyFab() {
        binding.fabEmergency.setOnClickListener(v -> startActivity(new Intent(this, EmergencyMessageActivity.class)));
        binding.fabAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserProfileActivity.class);
            intent.putExtra(UserProfileActivity.EXTRA_IS_NEW_USER, true);
            startActivity(intent);
        });
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setSelectedItemId(R.id.nav_dashboard);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                return true;
            }
            if (id == R.id.nav_livraisons) {
                startActivity(new Intent(this, AllDeliveriesActivity.class));
                return true;
            }
            if (id == R.id.nav_livreurs) {
                binding.scrollView.smoothScrollTo(0, binding.containerLivreurs.getTop());
                return true;
            }
            if (id == R.id.nav_messages) {
                startActivity(new Intent(this, MessagingScreenActivity.class));
                return true;
            }
            return false;
        });
    }

    private void observeViewModel() {
        viewModel.getStats().observe(this, this::renderStats);

        viewModel.getLivreurs().observe(this, users -> {
            List<User> safe = users == null ? new ArrayList<>() : users;
            renderLivreurs(safe);
        });

        viewModel.getAllDeliveries().observe(this, deliveries -> {
            List<Delivery> safe = deliveries == null ? new ArrayList<>() : deliveries;
            renderChart(safe);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void renderStats(DeliveryStats stats) {
        if (stats == null) {
            return;
        }
        binding.tvTotalStat.setText(String.valueOf(stats.getTotal()));
        binding.tvDeliveredStat.setText(String.valueOf(stats.getDelivered()));
        binding.tvInProgressStat.setText(String.valueOf(stats.getInProgress()));
        binding.tvFailedStat.setText(String.valueOf(stats.getFailed()));
    }

    private void renderLivreurs(List<User> users) {
        binding.containerLivreurs.removeAllViews();

        for (User user : users) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(0, dp(10), 0, dp(10));
            row.setClickable(true);
            row.setFocusable(true);
            int[] attrs = new int[]{android.R.attr.selectableItemBackground};
            android.content.res.TypedArray ta = obtainStyledAttributes(attrs);
            row.setBackground(ta.getDrawable(0));
            ta.recycle();
            row.setOnClickListener(v -> openUserProfile(user.getId()));

            TextView avatar = new TextView(this);
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(dp(44), dp(44));
            avatar.setLayoutParams(avatarParams);
            avatar.setGravity(Gravity.CENTER);
            avatar.setTextColor(Color.WHITE);
            avatar.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            avatar.setTypeface(avatar.getTypeface(), android.graphics.Typeface.BOLD);
            avatar.setText(firstLetter(user.getName()));
            avatar.setBackground(circleDrawable(ContextCompat.getColor(this, R.color.colorSuccess)));

            LinearLayout nameBlock = new LinearLayout(this);
            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            blockParams.setMargins(dp(12), 0, 0, 0);
            nameBlock.setLayoutParams(blockParams);
            nameBlock.setOrientation(LinearLayout.VERTICAL);

            TextView name = new TextView(this);
            name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            name.setText(user.getName());
            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            name.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));
            name.setTypeface(name.getTypeface(), android.graphics.Typeface.BOLD);

            TextView email = new TextView(this);
            email.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            email.setText(user.getEmail() != null ? user.getEmail() : "");
            email.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
            email.setTextColor(ContextCompat.getColor(this, R.color.colorTextSecondary));

            nameBlock.addView(name);
            nameBlock.addView(email);

            TextView editHint = new TextView(this);
            LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            editHint.setLayoutParams(editParams);
            editHint.setText("›");
            editHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f);
            editHint.setTextColor(ContextCompat.getColor(this, R.color.colorTextSecondary));

            row.addView(avatar);
            row.addView(nameBlock);
            row.addView(editHint);
            binding.containerLivreurs.addView(row);
        }
    }

    private void openUserProfile(String userId) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(Constants.EXTRA_USER_ID, userId);
        startActivity(intent);
    }

    private void renderChart(List<Delivery> deliveries) {
        float[] weekCounts = new float[7];

        for (Delivery delivery : deliveries) {
            String scheduled = delivery.getScheduledTime();
            if (TextUtils.isEmpty(scheduled)) {
                continue;
            }
            try {
                int index = OffsetDateTime.parse(scheduled).getDayOfWeek().getValue() - 1;
                if (index >= 0 && index < 7) {
                    weekCounts[index] += 1f;
                }
            } catch (Exception ignored) {
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < weekCounts.length; i++) {
            entries.add(new BarEntry(i, weekCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        BarChart chart = binding.barChart;
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"}));
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);

        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setGranularity(1f);
        chart.getAxisRight().setEnabled(false);

        chart.animateY(1000);
        chart.invalidate();
    }

    private GradientDrawable circleDrawable(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        return drawable;
    }

    private String firstLetter(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "?";
        }
        return text.trim().substring(0, 1).toUpperCase(Locale.getDefault());
    }

    private int dp(int value) {
        return Math.round(getResources().getDisplayMetrics().density * value);
    }

    private void performLogout() {
        SessionManager.clearSession(this);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
