package org.games.geofox.geofox.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Ilja.Winokurow on 11.09.2015.
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceLauncher = new Intent(context, ServiceGPS.class);
            context.startService(serviceLauncher);
            Log.v(this.getClass().getName(), "Service loaded while device boot.");
        }
    }
}
