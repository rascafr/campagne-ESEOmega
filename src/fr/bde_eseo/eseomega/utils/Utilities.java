package fr.bde_eseo.eseomega.utils;

import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.InputFilter.LengthFilter;

@SuppressWarnings("unused")
// Put every userfull function inside
public class Utilities {
	
	
	
	private static final Uri EVENT_URI = CalendarContract.Events.CONTENT_URI;
	
	/**
	 * Adds the event to a calendar.
	 * 
	 * @param ctx
	 *            Context ( Please use the application context )
	 * @param title
	 *            title of the event
	 * @param dtstart
	 *            Start time: The value is the number of milliseconds since Jan.
	 *            1, 1970, midnight GMT.
	 * @param dtend
	 *            End time: The value is the number of milliseconds since Jan.
	 *            1, 1970, midnight GMT.
	 */
	
	private void addToCalendar(final Context ctx, final String title, final String content, final int calendarId,
			final int hour, final int minutes, final long timestamp) {

		final ContentResolver cr = ctx.getContentResolver();
		final ContentValues cv = new ContentValues();
		cv.put("calendar_id", calendarId);
		cv.put("title", title);
		cv.put("description", content);
		cv.put("dtstart", timestamp);
		cv.put("eventTimezone", TimeZone.getDefault().getID());
		cv.put("dtend", timestamp);
		cv.put("hasAlarm", 1);

		Uri newEvent;
		if (Build.VERSION.SDK_INT >= 8)
			newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);
		else
			newEvent = cr.insert(Uri.parse("content://com.android.calendar/events"), cv);

		if (newEvent != null) {
			final long id = Long.parseLong(newEvent.getLastPathSegment());
			final ContentValues values = new ContentValues();
			values.put("event_id", id);
			values.put("method", 1);
			values.put("hours", hour);
			values.put("minutes", minutes);
			if (Build.VERSION.SDK_INT >= 8)
				cr.insert(Uri.parse("content://com.android.calendar/reminders"), values);
			else
				cr.insert(Uri.parse("content://calendar/reminders"), values);

		}

	}
	/*
	public static void addEvent(Context ctx, String title, String description, String location, long dtstart, long dtend) {
		ContentResolver cr = ctx.getContentResolver();
		ContentValues cv = new ContentValues();
		cv.put(Events.CALENDAR_ID, 0);
		cv.put(Events.TITLE, title);
		cv.put(Events.DTSTART, dtstart);
		cv.put(Events.DTEND, dtend);
		cv.put(Events.EVENT_LOCATION, location);
		cv.put(Events.DESCRIPTION, description);
		cv.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
		cr.insert(buildEventUri(), cv);
	}
	
	//Builds the Uri for events (as a Sync Adapter)
	public static Uri buildEventUri() {
		return EVENT_URI
		.buildUpon()
		.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
		.appendQueryParameter(Calendars.ACCOUNT_NAME, ACCOUNT_NAME)
		.appendQueryParameter(Calendars.ACCOUNT_TYPE,
		CalendarContract.ACCOUNT_TYPE_LOCAL)
		.build();
	}*/
	
	// Remove white spaces and the first & last one
	public static String trimSpaces (String s) {
		
		if (s != null)
			if (s.length() > 0) {
			
				s = s.replaceAll("\\s+", " ");
				if (s.charAt(s.length()-1) == ' ') {
					s = s.substring(0, s.length() - 1);
				}
				if (s.charAt(0) == ' ') {
					s = s.substring(1);
				}
			}
		return s;
	}
}
