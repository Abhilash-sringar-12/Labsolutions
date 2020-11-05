package com.application.labsolutions.services;

import retrofit2.Call;
import retrofit2.Callback;

public class SendNotification {

    public SendNotification() {
    }

    public static void notify(String token, String title, String message, ApiService apiService, String classType) {
        try {
            if (token != null) {
                Data data = new Data(title, message, classType);
                NotificationSender sender = new NotificationSender(data, token);
                apiService.sendNotification(sender).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if (response.code() == 200) {

                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
