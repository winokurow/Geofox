package org.games.geofox.geofox.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class ServiceGPS extends Service {

public static int interval = 30000; // 10 sec
public static int first_run = 1000; // 5 seconds
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
        SharedPreferences sharedPref = this.getSharedPreferences("org.games.geofox.main", Context.MODE_PRIVATE);

        if (intent.getStringExtra("sessionid") != null) {
            sessionid = intent.getStringExtra("sessionid");
            version = intent.getStringExtra("version");
            url = intent.getStringExtra("url");
            interval = intent.getIntExtra("serviceinterval", 30000);
            first_run = intent.getIntExtra("servicefirstrun", 1000);

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("sessionid", sessionid);
            editor.putString("version", version);
            editor.putString("url", url);
            editor.putInt("serviceinterval", interval);
            editor.putInt("servicefirstrun", first_run);
            editor.commit();
        } else {
            sessionid = sharedPref.getString("sessionid", "");
            version = sharedPref.getString("version", "");
            url = sharedPref.getString("url", "");
            interval = sharedPref.getInt("serviceinterval", 30000);
            first_run = sharedPref.getInt("servicefirstrun", 1000);
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

    SharedPreferences sharedPref = this.getSharedPreferences("org.games.geofox.main", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPref.edit();


    editor.putString("sessionid", "");
    editor.putString("version", "");
    editor.putString("url", "");
    editor.putString("serviceinterval", "");
    editor.putString("servicefirstrun", "");

    editor.commit();
    if (alarmManager != null) {
        Intent intent = new Intent(this, RepeatingAlarmService.class);
        alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
        }
    Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
    Log.v(this.getClass().getName(), "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
        }

private void startService() {
    SharedPreferences sharedPref = this.getSharedPreferences("org.games.geofox.main", Context.MODE_PRIVATE);


    Intent intent = new Intent(this, RepeatingAlarmService.class);
    Date date = new Date();
    intent.putExtra("date", date.getTime());
    intent.putExtra("sessionid", sessionid);
    intent.putExtra("version", version);
    intent.putExtra("url", url);

    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + first_run,
                interval,
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

