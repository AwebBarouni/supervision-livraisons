package com.supervision.livraisons.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.supervision.livraisons.R;
import com.supervision.livraisons.dto.UserRequest;
import com.supervision.livraisons.model.User;
import com.supervision.livraisons.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends AndroidViewModel {

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<User> getUser() { return user; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadUser(android.content.Context context, String userId) {
        isLoading.setValue(true);
        ApiClient.getInstance(context).getUserById(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void loadMe(android.content.Context context) {
        isLoading.setValue(true);
        ApiClient.getInstance(context).getMe().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void updateUser(android.content.Context context, String userId, UserRequest request) {
        isLoading.setValue(true);
        saveSuccess.setValue(false);
        ApiClient.getInstance(context).updateUser(userId, request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body());
                    saveSuccess.setValue(true);
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void createUser(android.content.Context context, UserRequest request) {
        isLoading.setValue(true);
        saveSuccess.setValue(false);
        ApiClient.getInstance(context).createUser(request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    user.setValue(response.body());
                    saveSuccess.setValue(true);
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }
}
