package fr.bde_eseo.eseomega.receiver;

import fr.bde_eseo.eseomega.SplashScreen;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent("fr.bde_eseo.eseomega.SplashScreen");
            Intent i=new Intent(context, SplashScreen.class);
            PendingIntent pi= PendingIntent.getBroadcast(context, 0, i, 0);
        	Log.d("OmegaBOOT", "BOOT_COMPLETED received !");
        }
    }
}