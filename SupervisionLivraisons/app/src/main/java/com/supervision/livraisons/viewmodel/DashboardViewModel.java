package com.supervision.livraisons.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.supervision.livraisons.R;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.DeliveryStats;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {

    private final MutableLiveData<DeliveryStats> stats = new MutableLiveData<>();
    private final MutableLiveData<List<User>> livreurs = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Delivery>> allDeliveries = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public DashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<DeliveryStats> getStats() {
        return stats;
    }

    public LiveData<List<User>> getLivreurs() {
        return livreurs;
    }

    public LiveData<List<Delivery>> getAllDeliveries() {
        return allDeliveries;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadStats(Context context) {
        ApiClient.getInstance(context).getStats().enqueue(new Callback<DeliveryStats>() {
            @Override
            public void onResponse(@NonNull Call<DeliveryStats> call, @NonNull Response<DeliveryStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stats.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeliveryStats> call, @NonNull Throwable t) {
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void loadLivreurs(Context context) {
        ApiClient.getInstance(context).getLivreurs().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    livreurs.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void loadAllDeliveries(Context context) {
        ApiClient.getInstance(context).getDeliveries().enqueue(new Callback<List<Delivery>>() {
            @Override
            public void onResponse(@NonNull Call<List<Delivery>> call, @NonNull Response<List<Delivery>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allDeliveries.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Delivery>> call, @NonNull Throwable t) {
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }
}
