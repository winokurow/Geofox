package org.games.geofox;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.games.geofox.entities.GameStatus;
import org.games.geofox.entities.MemberData;
import org.games.geofox.geofox.service.ServiceGPS;
import org.games.geofox.org.games.geofox.data.MemberTyp;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapsActivity extends FragmentActivity  implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    DialogFragment dlg1;
    DialogFragment dlg2;
    public final static String BROADCAST_ACTION = "geofox.update";
    BroadcastReceiver br;
    private Map<Marker, MemberData> markersContent = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Start gps service
        Context context = this;
        Intent intentGpsService = new Intent(context, ServiceGPS.class);
        intentGpsService.putExtras(this.getIntent());
        startService(intentGpsService);

        Bundle args1 = new Bundle();
        args1.putString("text", "Waiting for hunters to join the game.");
        this.dlg1 = new StartFragment();
        this.dlg1.setArguments(args1);

        Bundle args2 = new Bundle();
        args2.putString("text", "Waiting for hunters distance will be more then 500m.");
        this.dlg2 = new StartFragment();
        this.dlg2.setArguments(args2);

        setContentView(R.layout.activity_maps);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map)).getMap();

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 1000, this);

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                // Getting the position from the marker
                MemberData position = markersContent.get(arg0);

                TextView tvName = (TextView) v.findViewById(R.id.tv_name);
                TextView tvAlt = (TextView) v.findViewById(R.id.tv_alt);
                TextView tvAcc = (TextView) v.findViewById(R.id.tv_acc);
                TextView tvSpeed = (TextView) v.findViewById(R.id.tv_speed);

                tvName.setText(String.format(getString(R.string.dialog_name), position.getName()));
                tvAlt.setText(String.format(getString(R.string.dialog_altitude), position.getAltitude()));
                tvAcc.setText(String.format(getString(R.string.dialog_accuracy), position.getAccuracy()));
                tvSpeed.setText(String.format(getString(R.string.dialog_speed), position.getSpeed()));
                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // создаем BroadcastReceiver
        br = new BroadcastReceiver() {

            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                markersContent = new HashMap<>();
                String message = intent.getStringExtra("message");
                Log.e("abdeds", message);
                if (message.contains("error")) {
                    Intent intent2 = new Intent(context, ServiceGPS.class);
                    stopService(intent2);

                    Intent intent1 = new Intent(context, MainActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent1);
                } else {
                GameStatus status = (GameStatus) intent.getSerializableExtra("gamestatus");
                    Log.e("abdeds status", status.toString());
                if (status.getHuntersposition().size() > 0) {
                    if (dlg1.isVisible()) {
                        dlg1.dismiss();
                        dlg2.show(getFragmentManager(), "dlg2");
                    }
                    boolean isDistance = true;
                    if (dlg2.isVisible()) {
                        float[] results = new float[1];
                        for (MemberData positionData : status.getHuntersposition()) {
                            Location.distanceBetween(status.getFoxposition().getLatitude(), status.getFoxposition().getLongitude(),
                                    positionData.getLatitude(), positionData.getLongitude(), results);
                            if (results[0] < 500.00) {
                                isDistance = false;
                            }
                        }
                    }
                    if (isDistance) {
                        if (dlg2.isVisible()) {
                            dlg2.dismiss();
                        }
                        if (status.getGamestatus() > 0) {
                            Intent intentEndActivity = new Intent(context, EndActivity.class);
                            intentEndActivity.putExtra("status", status.getGamestatus());
                            startActivity(intentEndActivity);
                        }

                        // output distance
                        outputDistance(status);

                        Iterator it = markersContent.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            ((Marker) pair.getKey()).remove();
                            System.out.println(pair.getKey() + " = " + pair.getValue());
                            it.remove(); // avoids a ConcurrentModificationException
                        }
                        mMap.clear();
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        if (status.getMyposition().getTyp().equals(MemberTyp.HUNTER)) {
                            setUpMap(status.getMyposition());
                            builder.include(new LatLng(status.getMyposition().getLatitude(), status.getMyposition().getLongitude()));
                        }
                        setUpFox(status.getFoxposition());
                        builder.include(new LatLng(status.getFoxposition().getLatitude(), status.getFoxposition().getLongitude()));
                        for (MemberData positionData : status.getHuntersposition()) {
                            setUpHunter(positionData);
                            builder.include(new LatLng(positionData.getLatitude(), positionData.getLongitude()));
                        }

                        LatLngBounds bounds = builder.build();
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                                70);
                        mMap.animateCamera(cu);
                    }

                } else {
                    if (!(dlg1.isVisible())) {
                        Log.e("abdeds dialog", "dialog is shown");
                        dlg1.show(getFragmentManager(), "dlg1");
                    }
                }
                }
            }
        };
        this.registerReceiver(br, new IntentFilter(BROADCAST_ACTION));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // дерегистрируем (выключаем) BroadcastReceiver
        unregisterReceiver(br);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //locationManager.requestLocationUpdates(bestProvider, 20000, 1, this);


    }

    /** Stop the updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(MemberData position) {
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(position.getLatitude(), position.getLongitude()))
                .radius(50)
                .strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT));

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(position.getLatitude(), position.getLongitude())).title("Marker").
                icon(BitmapDescriptorFactory.fromResource(R.drawable.greenmarker)));
        markersContent.put(marker, position);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpFox(MemberData position) {

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(position.getLatitude(), position.getLongitude())).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.redmarker)));
        markersContent.put(marker, position);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpHunter(MemberData position) {

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(position.getLatitude(), position.getLongitude()))
                .radius(50)
                .strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT));
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(position.getLatitude(), position.getLongitude())).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.bluemarker)));
        markersContent.put(marker, position);
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

    /**
     * Output distance
     *
     * @param status - game data
     */
    public void outputDistance(GameStatus status) {
        float distance = calculateDistance(status);
        String text = new DecimalFormat("#.##").format(distance);
        if (status.getMyposition().getTyp().equals(MemberTyp.HUNTER)) {
            text = "Distance to fox: " + text + " m";
        } else {
            text = "Minimal distance to hunter: " + text + " m";
        }
        TextView distanceTextView = (TextView) findViewById(R.id.distance);
        distanceTextView.setText(text);
    }

    /**
     * Calculate minimal distance between fox and hunter
     *
     * @param status - game data
     *
     * @return distance between fox and hunter
     */
    public static float calculateDistance(GameStatus status) {
        float[] results = new float[1];
        float distance = 0.00f;
        if (status != null) {
            if (status.getMyposition().getTyp().equals(MemberTyp.HUNTER)) {
                Location.distanceBetween(status.getMyposition().getLatitude(), status.getMyposition().getLongitude(),
                        status.getFoxposition().getLatitude(), status.getFoxposition().getLongitude(), results);
                distance = results[0];
            } else {
                for (MemberData positionData : status.getHuntersposition()) {
                    Location.distanceBetween(status.getMyposition().getLatitude(), status.getMyposition().getLongitude(),
                            positionData.getLatitude(), positionData.getLongitude(), results);
                    distance = distance < results[0] ? results[0] : distance;
                }
            }
        }
        return distance;
    }
}
