package org.games.geofox.geofox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import org.games.geofox.MapsActivity;
import org.games.geofox.entities.GameStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ilja.Winokurow on 11.09.2015.
 */
public class RepeatingAlarmService extends BroadcastReceiver implements LocationListener {

    public String sessionid;
    public String version;
    GameStatus retr;
    String test;
    Context context;

    @Override
    public void onReceive(Context context1, Intent intent) {
        context = context1;
        sessionid = intent.getExtras().getString("sessionid");
        String url = intent.getExtras().getString("url");
        version = intent.getExtras().getString("version");

        Log.e("+++++++++++++++DEBUG6", sessionid);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1000, this);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        double lat = 0.00;
        double lon = 0.00;
        double speed = 0.00;
        double accuracy = 0.00;
        double alt = 0.00;
        if (location != null) {
             lat = location.getLatitude();
             lon = location.getLongitude();
             speed = location.getSpeed();
             accuracy = location.getAccuracy();
             alt = location.getAltitude();
        }

        Log.e("abd", "Session Id." + sessionid);
        Toast.makeText(context, "It's Service Time!" + sessionid, Toast.LENGTH_LONG).show();
        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        try {
            obj.put("latitude", lat);
            obj.put("longitude", lon);
            obj.put("altitude",alt);
            obj.put("accuracy", accuracy);
            obj.put("speed", speed);
            obj1.put("position",obj );

        } catch (JSONException e) {
            Log.e("DEBUG123", e.getMessage());
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        Log.e("abd", "URL." + url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                ObjectMapper mapper = new ObjectMapper();
                AnnotationIntrospector introspector = new
                        JacksonAnnotationIntrospector();
                mapper.setAnnotationIntrospector(introspector);
                try {
                    retr = mapper.readValue(response.toString(), GameStatus.class);
                    test = retr.toString();
                    if (retr != null) {
                        Intent intent1 = new Intent(MapsActivity.BROADCAST_ACTION);
                        intent1.putExtra("gamestatus", retr);
                        intent1.putExtra("message", "ok");
                        try {
                            context.sendBroadcast(intent1);
                        } catch (Exception e) {
                            Log.e("DEBUG123", e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    Log.e("DEBUG123", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse  != null) {

                    if (error.networkResponse.headers.get("error") != null) {
                        Log.e("DEBUG123", error.networkResponse.headers.get("error"));

                        if (error.networkResponse.headers.get("error").contains("Not Authorized")) {
                            Log.e("DEBUG123", "here");

                            Intent intent1 = new Intent(MapsActivity.BROADCAST_ACTION);
                            intent1.putExtra("message", "error");
                            context.sendBroadcast(intent1);
                        }
                    }
                }
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("version", version);
                params.put("sessionid", sessionid);
                return params;
            }};

        queue.add(jsObjRequest);

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}