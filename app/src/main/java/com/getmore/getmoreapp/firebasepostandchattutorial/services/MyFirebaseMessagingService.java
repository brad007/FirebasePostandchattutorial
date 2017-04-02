package com.getmore.getmoreapp.firebasepostandchattutorial.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.getmore.getmoreapp.firebasepostandchattutorial.R;
import com.getmore.getmoreapp.firebasepostandchattutorial.ui.activities.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by brad on 2017/04/01.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String USERNAME = "username";
    private static final String IMAGEURL = "imageUrl";
    private static final String EMAIL = "email";
    private static final String UID = "uid";
    private static final String TEXT = "text";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size() > 0){
            Map<String, String> data = remoteMessage.getData();

            String username = data.get(USERNAME);
            String imageUrl = data.get(IMAGEURL);
            String email = data.get(EMAIL);
            String uid = data.get(UID);
            String text = data.get(TEXT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);

            //I'm only using username and text for this tutorial. but you can use the other
            // variables for when you have a custom view on your notifications and what to do
            // when the user clicks the notification
            mBuilder.setContentTitle(username);
            mBuilder.setContentText(text);

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

// Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
