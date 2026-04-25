package com.supervision.livraisons.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.supervision.livraisons.util.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        String token = SessionManager.getToken(context);

        if (token != null && !token.isEmpty()) {
            Request request = original.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(request);
        }

        return chain.proceed(original);
    }
}
