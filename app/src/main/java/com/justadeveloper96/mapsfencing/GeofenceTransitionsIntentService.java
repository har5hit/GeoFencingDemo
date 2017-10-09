package com.justadeveloper96.mapsfencing;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import static android.content.ContentValues.TAG;


/**
 * Created by Harshith on 10-10-2017.
 */

public class GeofenceTransitionsIntentService extends IntentService {
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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

            String message = (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) ? "You are in range" : "You are out of range";
            sendNotification(message);
        }
    }

    private void sendNotification(String geofenceTransitionDetails) {
        Intent openActivity=new Intent(this,MapsActivity.class);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(this);
        notificationCompatBuilder.setAutoCancel(true).
                setContentTitle(getString(R.string.app_name)).
                setContentText(geofenceTransitionDetails).
                setSmallIcon(android.R.drawable.ic_menu_mylocation).
                setContentIntent(PendingIntent.getActivity(this,0,openActivity,0)).
                build();
    }


}
