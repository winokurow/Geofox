package org.games.geofox.geofox.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class ServiceGPS extends Service {

public static final int INTERVAL = 30000; // 10 sec
public static final int FIRST_RUN = 1; // 5 seconds
        int REQUEST_CODE = 11223344;
public String sessionid;
    public String version;
    public String url;
        AlarmManager alarmManager;

    private final IBinder binder = new ServiceBinder();

    public class ServiceBinder extends Binder {

        public ServiceGPS getService() {

            return ServiceGPS.this;
        }
    }
@Override
public void onCreate() {
        super.onCreate();
        }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (intent.getStringExtra("sessionid") != null) {
            sessionid = intent.getStringExtra("sessionid");
            version = intent.getStringExtra("version");
            url = intent.getStringExtra("url");
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("sessionid", sessionid);
            editor.putString("version", version);
            editor.putString("url", url);
            editor.commit();
        } else {
            sessionid = sharedPref.getString("sessionid", "");
            version = sharedPref.getString("version", "");
            url = sharedPref.getString("url", "");
        }
        startService();
        return START_NOT_STICKY;
    }

    @Override
public IBinder onBind(Intent intent) {
        onCreate();
        return null;
        }

@Override
public void onDestroy() {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    SharedPreferences.Editor editor = sharedPref.edit();
    editor.putString("sessionid", "");
    editor.putString("version", "");
    editor.putString("url", "");
    editor.commit();
    if (alarmManager != null) {
        Intent intent = new Intent(this, RepeatingAlarmService.class);
        alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
        }
    Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
    Log.v(this.getClass().getName(), "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
        }

private void startService() {
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);


    Intent intent = new Intent(this, RepeatingAlarmService.class);


    intent.putExtra("sessionid", sessionid);
    intent.putExtra("version", version);
    intent.putExtra("url", url);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + FIRST_RUN,
        INTERVAL,
        pendingIntent);


        }

    public void setSessionId(String a) {
        this.sessionid = a;
    }
    public void setUrl(String a) {
        this.url = a;
    }
    public void setVersion(String a) {
        this.version = a;
    }


        }

