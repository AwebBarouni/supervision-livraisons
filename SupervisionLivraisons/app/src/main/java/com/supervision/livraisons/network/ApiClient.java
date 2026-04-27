package com.supervision.livraisons.network;

import android.content.Context;

import com.supervision.livraisons.util.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static Retrofit instance;
    private static String activeBaseUrl;

    private ApiClient() {
    }

    public static ApiService getInstance(Context context) {
        String baseUrl = Constants.getBaseUrl(context.getApplicationContext());
        if (instance == null || !baseUrl.equals(activeBaseUrl)) {
            synchronized (ApiClient.class) {
                if (instance == null || !baseUrl.equals(activeBaseUrl)) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(context))
                            .addInterceptor(loggingInterceptor)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

                    instance = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
                    activeBaseUrl = baseUrl;
                }
            }
        }
        return instance.create(ApiService.class);
    }

    public static void reset() {
        synchronized (ApiClient.class) {
            instance = null;
            activeBaseUrl = null;
        }
    }
}
