package com.supervision.livraisons.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.supervision.livraisons.R;
import com.supervision.livraisons.dto.SendMessageRequest;
import com.supervision.livraisons.model.Conversation;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Conversation>> conversations = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> sendSuccess = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public MessageViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Conversation>> getConversations() {
        return conversations;
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getSendSuccess() {
        return sendSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadConversations(Context context) {
        isLoading.setValue(true);
        ApiClient.getInstance(context).getConversations().enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(@NonNull Call<List<Conversation>> call, @NonNull Response<List<Conversation>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    conversations.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Conversation>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void loadMessages(Context context, String userId) {
        isLoading.setValue(true);
        ApiClient.getInstance(context).getMessages(userId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(@NonNull Call<List<Message>> call, @NonNull Response<List<Message>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    messages.setValue(response.body());
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Message>> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void sendMessage(Context context, SendMessageRequest request) {
        sendSuccess.setValue(false);
        isLoading.setValue(true);
        ApiClient.getInstance(context).sendMessage(request).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    sendSuccess.setValue(true);
                } else {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }

    public void markAsRead(Context context, String id) {
        ApiClient.getInstance(context).markAsRead(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!response.isSuccessful()) {
                    errorMessage.setValue(getApplication().getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                errorMessage.setValue(getApplication().getString(R.string.error_network));
            }
        });
    }
}
