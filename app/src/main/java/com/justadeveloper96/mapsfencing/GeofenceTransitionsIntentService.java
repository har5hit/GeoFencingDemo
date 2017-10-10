package com.justadeveloper96.mapsfencing;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.Random;

/**
 * Created by Harshith on 10-10-2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    private static final String TAG = "GeofenceTransitionsInte";
    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: transition observed");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            String message = (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) ? "You are inside the zone" : "You are out of the zone";
            sendNotification(message);
        }
    }

    private void sendNotification(String geofenceTransitionDetails) {
        Intent openActivity=new Intent(this,MapsActivity.class);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationCompatBuilder.setAutoCancel(true)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(geofenceTransitionDetails)
                .setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setSound(defaultSoundUri)
                .setContentIntent(PendingIntent.getActivity(this,0,openActivity,PendingIntent.FLAG_UPDATE_CURRENT)).
                build();
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int n=new Random().nextInt();
        manager.notify(n,notificationCompatBuilder.build());
    }


}
