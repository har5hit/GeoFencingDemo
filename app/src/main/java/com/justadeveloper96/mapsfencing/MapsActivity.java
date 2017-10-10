package com.justadeveloper96.mapsfencing;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.justadeveloper96.mapsfencing.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PermissionHelper.PermissionsListener,ConfigurationDialog.OnFragmentInteractionListener {

    private static final int REQUEST_CHECK_SETTINGS = 543;
    private GoogleMap mMap;

    PermissionHelper permissionHelper;
    private static final String TAG = "MapsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        permissionHelper = new PermissionHelper(this);
        permissionHelper.setListener(this);
        startService(new Intent(this,GeoFencingService.class));
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getPermissions();
        // Add a marker in Sydney and move the camera
    }

    @Override
    public void onPermissionGranted(int request_code) {
        showLocationPopup();
    }

    private void startWork() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);

        LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location==null) {
                    Log.d(TAG, "onSuccess: location null from fuse");
                    LatLng mumbai = new LatLng(19.105233, 72.883236);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mumbai,13));
                    return;
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
                        18));
            }
        });

        if (SharedPrefs.getPrefs(this).getBoolean(SharedPrefs.KEY_IN_PROGRESS)) {
            double lat = Double.parseDouble(SharedPrefs.getPrefs(this).getString(SharedPrefs.KEY_LAT));
            double lng = Double.parseDouble(SharedPrefs.getPrefs(this).getString(SharedPrefs.KEY_LNG));
            float metres = SharedPrefs.getPrefs(this).getFloat(SharedPrefs.KEY_METERS);
            LatLng latLng=new LatLng(lat,lng);
            mMap.addCircle(new CircleOptions().center(latLng).radius(metres).fillColor(0x443F51B5).strokeColor(0x883F51B5));

        }
    }


    public void setStartPositionAndCircle(float metres){
        LatLng latLng=new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        mMap.addCircle(new CircleOptions().center(latLng).radius(metres).fillColor(0x443F51B5).strokeColor(0x883F51B5));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_LAT, String.valueOf(latLng.latitude));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_LNG, String.valueOf(latLng.longitude));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_METERS, (metres));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_IN_PROGRESS, true);
        EventBus.getDefault().post(new GeoFenceEvent("ranging",latLng.latitude,latLng.longitude,metres,1000*60*30,true));
    }

    @Override
    public void onPermissionRejectedManyTimes(@NonNull List<String> rejectedPerms, int request_code) {
        new AlertDialog.Builder(this).setTitle("Permissions Required to access location").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getPermissions();
            }
        }).show();
    }

    public void getPermissions(){
        permissionHelper.requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
    }

    @Override
    public void onFragmentInteraction(boolean start, float meters) {
        if (start)
        {
            setStartPositionAndCircle(meters);
        }else {
            SharedPrefs.getPrefs(this).logout();
            mMap.clear();
            EventBus.getDefault().post(new GeoFenceEvent("1",3,3,4,5,false));
        }
    }

    public void configure(View v)
    {
        ConfigurationDialog dialog=ConfigurationDialog.newInstance(
                SharedPrefs.getPrefs(this).getBoolean(SharedPrefs.KEY_IN_PROGRESS),
                SharedPrefs.getPrefs(this).getFloat(SharedPrefs.KEY_METERS)
        );
        dialog.show(getSupportFragmentManager(),"");
    }

    public void showLocationPopup()
    {
        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    Log.d(TAG, "onComplete: ");
                    startWork();
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        MapsActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "onActivityResult: ");
                        startWork();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(this,"Location is necessary",Toast.LENGTH_SHORT).show();
                        // The user was asked to change settings, but chose not to
                        showLocationPopup();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
