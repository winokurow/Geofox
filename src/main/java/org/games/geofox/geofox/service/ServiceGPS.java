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

import org.games.geofox.entities.GameData;

import java.util.Date;

public class ServiceGPS extends Service {

    GameData gameData;

    int REQUEST_CODE = 11223344;
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
        gameData = new GameData();
        SharedPreferences sharedPref = this.getSharedPreferences("org.games.geofox.main", Context.MODE_PRIVATE);

        if (intent.getStringExtra("sessionid") != null) {
            gameData.setSessionID(intent.getStringExtra("sessionid"));
            gameData.setVersion(intent.getStringExtra("version"));
            gameData.setUrl(intent.getStringExtra("url"));
            gameData.setServiceinterval(intent.getIntExtra("serviceinterval", 30000));
            gameData.setServicefirstrun(intent.getIntExtra("servicefirstrun", 1000));
            gameData.setGamelength(intent.getIntExtra("gamelength", 180));

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString("sessionid", gameData.getSessionID());
            editor.putString("version", gameData.getVersion());
            editor.putString("url", gameData.getUrl());
            editor.putInt("serviceinterval", gameData.getServiceinterval());
            editor.putInt("servicefirstrun", gameData.getServicefirstrun());
            editor.putInt("gamelength", gameData.getGamelength());
            editor.commit();
        } else {
            gameData.setSessionID(sharedPref.getString("sessionid", ""));
            gameData.setVersion(sharedPref.getString("version", ""));
            gameData.setUrl(sharedPref.getString("url", ""));
            gameData.setServiceinterval(sharedPref.getInt("serviceinterval", 30000));
            gameData.setServicefirstrun(sharedPref.getInt("servicefirstrun", 1000));
            gameData.setGamelength(sharedPref.getInt("gamelength", 180));
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
    editor.putString("gamelength", "");

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
    intent.putExtra("sessionid", gameData.getSessionID());
    intent.putExtra("version", gameData.getVersion());
    intent.putExtra("url", gameData.getUrl());
    intent.putExtra("gamelength", gameData.getGamelength());


    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + gameData.getServicefirstrun(),
                gameData.getServiceinterval(),
        pendingIntent);
        }
        }

