package com.supervision.livraisons.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.supervision.livraisons.R;
import com.supervision.livraisons.dto.LoginRequest;
import com.supervision.livraisons.dto.LoginResponse;
import com.supervision.livraisons.network.ApiClient;
import com.supervision.livraisons.util.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthViewModel extends AndroidViewModel {

    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(Context context, String email, String password) {
        isLoading.setValue(true);
        String baseUrl = Constants.getBaseUrl(context);
        ApiClient.getInstance(context).login(new LoginRequest(email, password)).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    loginResult.setValue(response.body());
                } else if (response.code() == 401) {
                    errorMessage.setValue(getApplication().getString(R.string.error_login));
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_network)
                            + " (HTTP " + response.code() + " | " + baseUrl + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                String detail = t.getLocalizedMessage();
                if (detail == null || detail.isBlank()) {
                    errorMessage.setValue(getApplication().getString(R.string.error_network) + " (" + baseUrl + ")");
                    return;
                }
                errorMessage.setValue(getApplication().getString(R.string.error_network)
                        + " (" + baseUrl + " | " + detail + ")");
            }
        });
    }
}
