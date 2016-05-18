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

    /** Dialog that shown when awaiting for Hunters*/
    private DialogFragment dlgAwaitForHunter = new StartFragment();

    /** Dialog that shown when awaiting for Hunters distance>distance_max*/
    private DialogFragment dlgAwaitForHunterDistance = new StartFragment();

    public final static String BROADCAST_ACTION = "geofox.update";
    BroadcastReceiver br;

    // List of all map markers
    private Map<Marker, MemberData> markersContent = new HashMap<>();

    /**
     * Constructor
     */
    public MapsActivity() {
        super();
        Bundle bundle = new Bundle();
        bundle.putString("text", getString(R.string.map_popupWaitForHunterEnter_message));
        this.dlgAwaitForHunter.setArguments(bundle);

        bundle = new Bundle();
        bundle.putString("text", getString(R.string.map_popupWaitForHunterDistance_message));
        this.dlgAwaitForHunterDistance.setArguments(bundle);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Context context = this;
        int isRunning = this.getIntent().getIntExtra("isRunning", 0);
        if (isRunning == 0) {
            dlgAwaitForHunter.show(getFragmentManager(), "dlgAwaitForHunter");
        }
        // Start gps service
        Intent intentGpsService = new Intent(context, ServiceGPS.class);
        intentGpsService.putExtras(this.getIntent());
        startService(intentGpsService);

        setContentView(R.layout.activity_maps);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map)).getMap();

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, 1000, this);

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new infoPopup());

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
                    updateMap(status, context);
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
     * @return distance between fox and hunter. 0 when status is null
     */
    private static float calculateDistance(GameStatus status) {
        float distance = 0.00f;

        if (status != null) {
            Location from = new Location("From");
            from.setLatitude(status.getMyposition().getLatitude());
            from.setLongitude(status.getMyposition().getLongitude());
            if (status.getMyposition().getTyp().equals(MemberTyp.HUNTER)) {
                Location target = new Location("Target");
                target.setLatitude(status.getFoxposition().getLatitude());
                target.setLongitude(status.getFoxposition().getLongitude());
                distance = from.distanceTo(target);
            } else {
                for (MemberData positionData : status.getHuntersposition()) {
                    Location target = new Location("Target");
                    target.setLatitude(positionData.getLatitude());
                    target.setLongitude(positionData.getLongitude());
                    float result = from.distanceTo(target);
                    distance = distance < result ? result : distance;
                }
            }
        }
        return distance;
    }

    /**
     * Info Popup that shows player data (name, speed, altitude, accuracy).
     */
 class infoPopup implements GoogleMap.InfoWindowAdapter {

     // view
     private final View myContentsView;

     // constructor
     infoPopup(){
         // Getting view from the layout file info_window_layout
          myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
     }

     // Use default InfoWindow frame
     @Override
     public View getInfoWindow(Marker arg0) {
         return null;
     }

     // Defines the contents of the InfoWindow
     @Override
     public View getInfoContents(Marker arg0) {

         // Getting the position from the marker
         if (markersContent.containsKey(arg0)) {
             MemberData position = markersContent.get(arg0);

             TextView tvName = (TextView) myContentsView.findViewById(R.id.tv_name);
             TextView tvAlt = (TextView) myContentsView.findViewById(R.id.tv_alt);
             TextView tvAcc = (TextView) myContentsView.findViewById(R.id.tv_acc);
             TextView tvSpeed = (TextView) myContentsView.findViewById(R.id.tv_speed);

             String name = "";
             if (position.getName() != null) {
                 name = position.getName();
             }
             tvName.setText(String.format(getString(R.string.dialog_name), name));

             tvAlt.setText(String.format(getString(R.string.dialog_altitude), position.getAltitude()));
             tvAcc.setText(String.format(getString(R.string.dialog_accuracy), position.getAccuracy()));
             tvSpeed.setText(String.format(getString(R.string.dialog_speed), position.getSpeed()));
         }
         // Returning the view containing InfoWindow contents
         return myContentsView;

     }
 }
    /**
     * Update the map after request acquire
     * <p/>
     * @param status - game status
     * @param context - current context
     */
    private void updateMap(GameStatus status, Context context) {
        if (status.getGamestatus() == 10) {
            dlgAwaitForHunter.show(getFragmentManager(), "dlgAwaitForHunter");
        }
        if (status.getGamestatus() == 20) {
            dlgAwaitForHunter.dismiss();
            dlgAwaitForHunterDistance.show(getFragmentManager(), "dlg2");
        }

        if (status.getGamestatus() == 30) {
            if (dlgAwaitForHunterDistance.isVisible()) {
                dlgAwaitForHunterDistance.dismiss();
            }
        }
        if (status.getGamestatus() > 30) {
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
}
