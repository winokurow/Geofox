package org.games.geofox.geofox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ilja.Winokurow on 11.09.2015.
 */
public class RepeatingAlarmService extends BroadcastReceiver {

    public String sessionid;
    public String version;

    @Override
    public void onReceive(Context context, Intent intent) {
        sessionid = intent.getExtras().getString("sessionid");
        String url = intent.getExtras().getString("url");
        version = intent.getExtras().getString("version");

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        double speed = location.getSpeed();
        double accuracy = location.getAccuracy();
        double alt = location.getAltitude();
/*        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
        } catch (NullPointerException e) {
            lat = -1.0;
            lon = -1.0;
        }*/

        Log.d("abd", "Session Id." + sessionid);
        Toast.makeText(context, "It's Service Time!" + sessionid, Toast.LENGTH_LONG).show();
        JSONObject obj = new JSONObject();
        JSONObject obj1 = new JSONObject();
        try {
            obj.put("coordx", lat);
            obj.put("coordy", lon);
            obj.put("altitude",alt);
            obj.put("accuracy", accuracy);
            obj.put("speed", speed);
            obj1.put("position",obj );

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, obj1, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                    Log.e("abd", "Service error");
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
        Log.v(this.getClass().getName(), "Timed alarm onReceive() started at time: " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }
}