package com.justadeveloper96.mapsfencing;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Harshith on 20/1/17.
 */

public class SharedPrefs {
    private static SharedPrefs sharedPrefs;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    public static final String KEY_IN_PROGRESS = "IN_PROGRESS";
    public static final String KEY_LAT = "LAT";
    public static final String KEY_LNG = "LNG";
    public static final String KEY_METERS = "METERS";

    public SharedPrefs(Context c) {
        editor = c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE).edit();
        prefs = c.getSharedPreferences(c.getPackageName(), Context.MODE_PRIVATE);
    }

    public void save(String id,String value)
    {
        editor.putString(id,value).commit();
    }
    public void save(String id,Boolean value)
    {
        editor.putBoolean(id,value).commit();
    }
    public void save(String id,int value)
    {
        editor.putInt(id,value).commit();
    }
    public void save(String id,long value)
    {
        editor.putLong(id,value).commit();
    }

    public String getString(String id)
    {
        return prefs.getString(id,"");
    }
    public Long getLong(String id)
    {
        return prefs.getLong(id,0);
    }
    public int getInt(String id)
    {
        return prefs.getInt(id,0);
    }

    public float getFloat(String id)
    {
        return prefs.getFloat(id,0);
    }
    public boolean getBoolean(String id)
    {
        return prefs.getBoolean(id,false);
    }


    public void logout()
    {
        editor.clear().commit();
    }
    public static SharedPrefs getPrefs(Context context)
    {
        if(sharedPrefs==null)
        {
            sharedPrefs=new SharedPrefs(context);
        }
        return sharedPrefs;
    }

    public void setPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    public void removeListener(SharedPreferences.OnSharedPreferenceChangeListener listener)
    {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void save(String id, float value) {
        editor.putFloat(id,value).commit();
    }
}