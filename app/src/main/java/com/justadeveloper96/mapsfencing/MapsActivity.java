package com.justadeveloper96.mapsfencing;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PermissionHelper.PermissionsListener {

    private GoogleMap mMap;

    PermissionHelper permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        permissionHelper = new PermissionHelper(this);
        permissionHelper.setListener(this);
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
        if (SharedPrefs.getPrefs(this).getBoolean(SharedPrefs.KEY_IN_PROGRESS)) {
            double lat = Double.parseDouble(SharedPrefs.getPrefs(this).getString(SharedPrefs.KEY_LAT));
            double lng = Double.parseDouble(SharedPrefs.getPrefs(this).getString(SharedPrefs.KEY_LAT));
            float metres = SharedPrefs.getPrefs(this).getFloat(SharedPrefs.KEY_LAT);
            LatLng latLng=new LatLng(lat,lng);
            mMap.addCircle(new CircleOptions().center(latLng).radius(metres));
        }
    }


    public void setStartPositionAndCircle(float metres){
        LatLng latLng=new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
        mMap.addCircle(new CircleOptions().center(latLng).radius(metres));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_LAT, String.valueOf(latLng.latitude));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_LNG, String.valueOf(latLng.longitude));
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_METERS, metres);
        SharedPrefs.getPrefs(this).save(SharedPrefs.KEY_IN_PROGRESS, true);
        EventBus.getDefault().postSticky(new GeoFenceEvent("ranging",latLng.latitude,latLng.longitude,metres,1000*60*30));
    }

    @Override
    public void onPermissionRejectedManyTimes(@NonNull List<String> rejectedPerms, int request_code) {
        new AlertDialog.Builder(this).setTitle("Permissions Required to access location").setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getPermissions();
            }
        });

    }

    public void getPermissions(){
        permissionHelper.requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},200);
    }
}