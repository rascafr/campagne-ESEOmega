package fr.bde_eseo.eseomega.service;

import java.util.Calendar;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import fr.bde_eseo.eseomega.service.task.AlarmTask;

public class ScheduleService extends Service {

	/**
	 * Class for clients to access
	 */
	public class ServiceBinder extends Binder {
		ScheduleService getService() {
			Log.d("ScheduleService", "Null ? " + ((ScheduleService.this==null)?"yes":"no"));
			return ScheduleService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ScheduleService", "Received start id " + startId + ": " + intent);
		
		// We want this service to continue running until it is explicitly stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	private final IBinder mBinder = new ServiceBinder();

	/**
	 * Show an alarm for a certain date when the alarm is called it will pop up a notification
	 */
	public void setAlarm(Calendar c) {
		// This starts a new thread to set the alarm
		// You want to push off your tasks onto a new thread to free up the UI to carry on responding
		new AlarmTask(this, c).run();
	}
}