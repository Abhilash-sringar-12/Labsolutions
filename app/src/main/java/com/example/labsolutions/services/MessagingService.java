package com.example.labsolutions.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;

import com.example.labsolutions.R;
import com.example.labsolutions.admin.AllActivities;
import com.example.labsolutions.admin.MainActivity;
import com.example.labsolutions.customer.CurrentCustomerActivity;
import com.example.labsolutions.engineer.AssignedActivities;
import com.example.labsolutions.workadmin.WorkAdminAssignActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MessagingService extends FirebaseMessagingService {
    String title, message, classType;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotificationChannel();
        title = remoteMessage.getData().get("title");
        message = remoteMessage.getData().get("message");
        classType = remoteMessage.getData().get("activityType");
        Intent resultIntent = new Intent(this, getActivity(classType));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Labsolutions")
                .setSmallIcon(R.drawable.logo_notification)
                .setContentTitle(title);
        builder.setContentIntent(resultPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(message);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify((int) (100 + Math.random() * 1000), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Labsolutions";
            String description = "Labsolutions";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Labsolutions", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Class getActivity(String classType) {
        if (classType.equals("adminAllActivities")) {
            return AllActivities.class;
        } else if (classType.equals("workAdminAssignActivity")) {
            return WorkAdminAssignActivity.class;
        } else if (classType.equals("customerCurrentActivity")) {
            return CurrentCustomerActivity.class;
        } else if (classType.equals("engineerAssignActivity")) {
            return AssignedActivities.class;
        } else if (classType.equals("customerAllActivities")) {
            return com.example.labsolutions.customer.AllActivities.class;
        } else if (classType.equals("engineerAllActivities")) {
            return com.example.labsolutions.engineer.AllActivities.class;
        } else if (classType.equals("workAdminAllActivities")) {
            return com.example.labsolutions.workadmin.AllActivities.class;
        }
        return MainActivity.class;
    }
}
