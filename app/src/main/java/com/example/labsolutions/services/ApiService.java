package com.example.labsolutions.services;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:Key=AAAAYg4sE-k:APA91bH7O9UR3el_ZFdFvEVqRpAEQx2KaKUgQSzMoxqF2KIKsLoQGiGYET5A49Zh2VeYU2yo2AJVyQBsWs2gp0rLj_JFmPHotGinSb45GQzRl3_Wx2_3fp1YDnGqlK_X8ujl393jmO9Q"

            }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body NotificationSender notificationSender);
}
