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
import com.google.android.gms.maps.model.MarkerOptions;

import org.games.geofox.entities.GameStatus;
import org.games.geofox.entities.PositionData;
import org.games.geofox.geofox.service.ServiceGPS;

public class MapsActivity extends FragmentActivity  implements LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private String bestProvider;
    Location loc;
    DialogFragment dlg1;
    public final static String BROADCAST_ACTION = "geofox.update";
    BroadcastReceiver br;


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
        setUpMap(currentLatitude, currentLongitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currentLatitude, currentLongitude))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // создаем BroadcastReceiver
        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {
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
                    mMap.clear();
                    double radius = 0.0;
                    float[] results = new float[1];
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if ((status.getFoxposition().getLatitude() != status.getMyposition().getLatitude())
                            && (status.getFoxposition().getLongitude() != status.getMyposition().getLongitude())) {
                        setUpMap(status.getMyposition().getLatitude(), status.getMyposition().getLongitude());
                        builder.include(new LatLng(status.getMyposition().getLatitude(), status.getMyposition().getLongitude()));
                    }
                    setUpFox(status.getFoxposition().getLatitude(), status.getFoxposition().getLongitude());
                    builder.include(new LatLng(status.getFoxposition().getLatitude(), status.getFoxposition().getLongitude()));
                    for (PositionData positionData : status.getHuntersposition()) {
                        setUpHunter(positionData.getLatitude(), positionData.getLongitude());
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
    private void setUpMap(double lat, double lon) {
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(50)
                .strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.greenmarker)));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpFox(double lat, double lon) {

        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.redmarker)));
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpHunter(double lat, double lon) {

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lon))
                .radius(50)
                .strokeColor(Color.BLACK)
                .fillColor(Color.TRANSPARENT));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.bluemarker)));

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
