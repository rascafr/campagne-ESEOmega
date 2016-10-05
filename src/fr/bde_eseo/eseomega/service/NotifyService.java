package fr.bde_eseo.eseomega.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import fr.bde_eseo.eseomega.MainActivity;
import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.ui.phone.SecondActivity;

/**
 * This service is started when an Alarm has been raised
 * 
 * We pop a notification into the status bar for the user to click on
 * When the user clicks the notification a new activity is opened
 * 
 * @author paul.blundell
 */
public class NotifyService extends Service {

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		NotifyService getService() {
			return NotifyService.this;
		}
	}

	// Unique id to identify the notification.
	private static final int NOTIFICATION = 123;
	// Name of an intent extra we can use to identify if this service was started to create a notification	
	public static final String INTENT_NOTIFY = "fr.bde_eseo.eseomega.service.INTENT_NOTIFY";
	// The system notification manager
	private NotificationManager mNM;

	@Override
	public void onCreate() {
		Log.i("NotifyService", "onCreate()");
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("LocalService", "Received start id " + startId + ": " + intent);
		
		// If this service was started by out AlarmTask intent then we want to show our notification
		if(intent.getBooleanExtra(INTENT_NOTIFY, false))
			showNotification();
		
		// We don't care if this service is stopped as we have already delivered our notification
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Creates a notification and shows it in the OS drag-down status bar
	 */
	private void showNotification() {
		
		CharSequence title = "Rallye Appart ESEOmega";
		int icon = R.drawable.icon2;
		CharSequence text = "Les adresses sont dévoilées !";
		CharSequence textLong = "Les adresses sont dévoilées ! Allez jeter un coup d'oeil à l'application, et venez nombreux ce soir !";
		long time = System.currentTimeMillis();
		
		// The PendingIntent to launch our activity if the user selects this notification
		Intent intentData = new Intent(this, MainActivity.class);
		intentData.putExtra("notifIsOpen", 1); // 1 -> Rallye Appart
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intentData, PendingIntent.FLAG_UPDATE_CURRENT);
		
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(this)
			    .setSmallIcon(R.drawable.ic_omega)
			    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif))
			    .setContentTitle(title)
			    .setContentText(text)
			    .setAutoCancel(true)
			    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
			    .setDefaults(Notification.DEFAULT_VIBRATE)
			    .setLights(Color.CYAN, 750, 3500)
			    .setContentIntent(contentIntent)
			    .setStyle(new NotificationCompat.BigTextStyle()
                .bigText(textLong));
		
		mNM.notify(NOTIFICATION, mBuilder.build());
		
		
		// Stop the service when we are finished
		stopSelf();
	}
}