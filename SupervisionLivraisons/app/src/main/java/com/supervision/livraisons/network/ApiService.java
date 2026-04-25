package com.supervision.livraisons.network;

import com.supervision.livraisons.dto.LoginRequest;
import com.supervision.livraisons.dto.LoginResponse;
import com.supervision.livraisons.dto.SendMessageRequest;
import com.supervision.livraisons.dto.SyncUpdateStatusRequest;
import com.supervision.livraisons.dto.UpdateStatusRequest;
import com.supervision.livraisons.model.Conversation;
import com.supervision.livraisons.model.Delivery;
import com.supervision.livraisons.model.DeliveryStats;
import com.supervision.livraisons.model.Message;
import com.supervision.livraisons.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("users/me")
    Call<User> getMe();

    @GET("users/livreurs")
    Call<List<User>> getLivreurs();

    @GET("deliveries")
    Call<List<Delivery>> getDeliveries();

    @GET("deliveries/today")
    Call<List<Delivery>> getTodayDeliveries();

    @GET("deliveries/stats")
    Call<DeliveryStats> getStats();

    @GET("deliveries/{id}")
    Call<Delivery> getDelivery(@Path("id") String id);

    @PATCH("deliveries/{id}/status")
    Call<Delivery> updateStatus(@Path("id") String id, @Body UpdateStatusRequest request);

    @GET("messages/conversations")
    Call<List<Conversation>> getConversations();

    @GET("messages/{userId}")
    Call<List<Message>> getMessages(@Path("userId") String userId);

    @POST("messages")
    Call<Message> sendMessage(@Body SendMessageRequest request);

    @PATCH("messages/{id}/read")
    Call<Void> markAsRead(@Path("id") String id);

    @GET("sync/daily/{driverId}")
    Call<List<Delivery>> getDailyDeliveries(@Path("driverId") String driverId,
                                            @Query("date") String date,
                                            @Query("lat") Double lat,
                                            @Query("lng") Double lng);

    @GET("sync/emergency-messages")
    Call<List<Message>> getEmergencyMessages();

    @POST("sync/update-status")
    Call<Delivery> updateStatusViaSync(@Body SyncUpdateStatusRequest request);

    @GET("emergency/client-search")
    Call<List<Delivery>> emergencyClientSearch(@Query("query") String query);
}
