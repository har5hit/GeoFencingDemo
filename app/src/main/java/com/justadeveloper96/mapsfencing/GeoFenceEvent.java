package com.justadeveloper96.mapsfencing;

/**
 * Created by Harshith on 10-10-2017.
 */

public class GeoFenceEvent {
    private String key;
    private double latitude;
    private double longitude;
    private float meters;
    long expiration_millis;

    public GeoFenceEvent(String key, double latitude, double longitude, float meters, long expiration_millis) {
        this.key = key;
        this.latitude = latitude;
        this.longitude = longitude;
        this.meters = meters;
        this.expiration_millis = expiration_millis;
    }

    public String getKey() {
        return key;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getMeters() {
        return meters;
    }

    public long getExpiration_millis() {
        return expiration_millis;
    }
}
