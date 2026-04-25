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

    private ApiClient() {
    }

    public static ApiService getInstance(Context context) {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
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
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(okHttpClient)
                            .build();
                }
            }
        }
        return instance.create(ApiService.class);
    }
}
