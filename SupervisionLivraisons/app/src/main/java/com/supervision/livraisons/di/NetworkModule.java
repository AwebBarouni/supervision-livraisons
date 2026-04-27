package com.supervision.livraisons.di;

import android.content.Context;

import com.supervision.livraisons.network.ApiService;
import com.supervision.livraisons.network.AuthInterceptor;
import com.supervision.livraisons.util.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public final class NetworkModule {

    private NetworkModule() {
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(@ApplicationContext Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(@ApplicationContext Context context, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.getBaseUrl(context))
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides
    @Singleton
    public static ApiService provideApiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }
}
