package org.games.geofox;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.games.geofox.entities.GameStatus;
import org.games.geofox.entities.PositionData;
import org.games.geofox.geofox.service.ServiceGPS;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapsActivity extends FragmentActivity  implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private String bestProvider;
    Location loc;
    DialogFragment dlg1;
    public final static String BROADCAST_ACTION = "geofox.update";
    BroadcastReceiver br;
    private Map<Marker, PositionData> markersContent = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dlg1 = new StartFragment();
        setContentView(R.layout.activity_maps);
        Context context = this;
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                R.id.map)).getMap();
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String mlocProvider;
        Criteria hdCrit = new Criteria();

        hdCrit.setAccuracy(Criteria.ACCURACY_COARSE);

        mlocProvider = locationManager.getBestProvider(hdCrit, true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1000, this);
        double currentLatitude = 0.0;
        double currentLongitude = 0.0;
        Location currentLocation = locationManager.getLastKnownLocation(mlocProvider);
        if (currentLocation != null) {
             currentLatitude = currentLocation.getLatitude();
             currentLongitude = currentLocation.getLongitude();
        }

        mMap.clear();
        setUpMap(new PositionData("", currentLatitude, currentLongitude));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currentLatitude, currentLongitude))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                PositionData position = markersContent.get(arg0);

                TextView tvName = (TextView) v.findViewById(R.id.tv_name);
                TextView tvAlt = (TextView) v.findViewById(R.id.tv_alt);
                TextView tvAcc = (TextView) v.findViewById(R.id.tv_acc);
                TextView tvSpeed = (TextView) v.findViewById(R.id.tv_speed);

                tvName.setText("Name: " + position.getName());
                tvAlt.setText("Altitude: " + position.getAltitude());
                tvAcc.setText("Accuracy: " + position.getAccuracy());
                tvSpeed.setText("Speed: " + position.getSpeed());
                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // создаем BroadcastReceiver
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
                markersContent = new HashMap<Marker, PositionData>();
                GameStatus status = (GameStatus) intent.getSerializableExtra("gamestatus");
                if (status.getGamestatus() != 10) {
                    if (dlg1.isVisible()) {
                        dlg1.dismiss();
                    }

                    if (status.getGamestatus() > 0) {
                        stopService(new Intent(context, ServiceGPS.class));
                        Intent intent1 = new Intent(context, EndActivity.class);
                        intent1.putExtra("status", status.getGamestatus());
                        startActivity(intent1);
                    }

                    Iterator it = markersContent.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        ((Marker)pair.getKey()).remove();
                        System.out.println(pair.getKey() + " = " + pair.getValue());
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    mMap.clear();

                    double radius = 0.0;
                    float[] results = new float[1];
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if ((status.getFoxposition().getLatitude() != status.getMyposition().getLatitude())
                            && (status.getFoxposition().getLongitude() != status.getMyposition().getLongitude())) {
                        setUpMap(status.getMyposition());
                        builder.include(new LatLng(status.getMyposition().getLatitude(), status.getMyposition().getLongitude()));
                    }
                    setUpFox(status.getFoxposition());
                    builder.include(new LatLng(status.getFoxposition().getLatitude(), status.getFoxposition().getLongitude()));
                    for (PositionData positionData : status.getHuntersposition()) {
                        setUpHunter(positionData);
                        builder.include(new LatLng(positionData.getLatitude(), positionData.getLongitude()));
                    }

                    LatLngBounds bounds = builder.build();
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                            70);
                    mMap.animateCamera(cu);
                } else {
                    if (!(dlg1.isVisible())) {
                        dlg1.show(getFragmentManager(), "dlg1");
                    }

                }
            }
        };
        this.registerReceiver(br, new IntentFilter(
                BROADCAST_ACTION));
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
    private void setUpMap(PositionData position) {
        Circle circle = mMap.addCircle(new CircleOptions()
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
    private void setUpFox(PositionData position) {

        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(position.getLatitude(), position.getLongitude())).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.redmarker)));
        markersContent.put(marker, position);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpHunter(PositionData position) {

        Circle circle = mMap.addCircle(new CircleOptions()
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


}
