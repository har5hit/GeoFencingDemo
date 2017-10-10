package com.justadeveloper96.mapsfencing;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class GeoFencingService extends Service {
    private GeofencingClient mGeofencingClient;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    private static final String TAG = "GeoFencingService";
    public GeoFencingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void addGeoFence(GeoFenceEvent geoFenceEvent) {
        Log.d(TAG,"geofence add"+geoFenceEvent.toString());
        initialize();
        mGeofenceList.clear();
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(geoFenceEvent.getKey())
                .setCircularRegion(
                        geoFenceEvent.getLatitude(), geoFenceEvent.getLongitude(),
                        geoFenceEvent.getMeters()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //mGeofencingClient.removeGeofences(getGeofencePendingIntent());

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Geofences added
                        // ...
                        Log.d(TAG,"geofence added success");
                        //Toast.makeText(getApplicationContext(),"GeoFence added",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add geofences
                        // ...
                        Log.d(TAG,"geofence added failed");
                        //Toast.makeText(getApplicationContext(),"GeoFence add fail",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private void initialize() {
        if (mGeofencingClient!=null)
        {
            return;
        }
        mGeofencingClient = LocationServices.getGeofencingClient(this);
        mGeofenceList=new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void removeGeoFence(GeoFenceEvent event)
    {
        Log.d(TAG,"geofence remove");
        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"geofence removed success");
                        //Toast.makeText(getApplicationContext(),"GeoFence removed",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"geofence removed failed");
                        //Toast.makeText(getApplicationContext(),"GeoFence remove failed",Toast.LENGTH_SHORT).show();
                        // Failed to remove geofences
                        // ...
                    }
                });

    }

    @Subscribe
    public void onGeoFenceEvent(GeoFenceEvent event)
    {
        Log.d(TAG,"got geofenceEvent");
        if (event.isActive())
        {
            addGeoFence(event);
        }else
        {
            removeGeoFence(event);
        }
    }
}
