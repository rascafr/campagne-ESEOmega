package fr.bde_eseo.eseomega;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.GetChars;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
// For web implementation
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import fr.bde_eseo.eseomega.adapter.NavDrawerListAdapter;
import fr.bde_eseo.eseomega.asynctasks.SimplePostAsyncTask;
import fr.bde_eseo.eseomega.fragments.ChallengeFragment;
import fr.bde_eseo.eseomega.fragments.TreasurePlay;
import fr.bde_eseo.eseomega.fragments.AnimationsFragment;
import fr.bde_eseo.eseomega.fragments.HomeFragment;
import fr.bde_eseo.eseomega.fragments.MediaSocialFragment;
import fr.bde_eseo.eseomega.fragments.ProgrammeFragment;
import fr.bde_eseo.eseomega.fragments.SponsorsFragment;
import fr.bde_eseo.eseomega.fragments.TeamFragment;
import fr.bde_eseo.eseomega.fragments.TreasureMenu;
import fr.bde_eseo.eseomega.model.NavDrawerItem;
import fr.bde_eseo.eseomega.service.ScheduleClient;
import fr.bde_eseo.eseomega.utils.Utilities;
import fr.bde_eseo.eseomega.youtubepackage.FragmentYoutube;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;

public class MainActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	// This is a handle so that we can call methods on our service
    private ScheduleClient scheduleClient;
	
	// App Version Number
	private String versionApp;
	private String serverVersionApp = "0.0";
	
	// Debug mode for date simulation
	private static boolean debugMode = true;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	
	private boolean treasureDays = false, playDaysAreOver = false;
	
	// Preference strings
	private static String PREFS_ID = "mPrefsRegister";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get intents
		Bundle extras = getIntent().getExtras(); 
		int notifType = 0;

		if (extras != null) {
			notifType = extras.getInt("notifIsOpen");
			versionApp = extras.getString("versionApp");
			serverVersionApp = extras.getString("versionServer");
		}
		
		// Create a new service client and bind our activity to this service
        scheduleClient = new ScheduleClient(this);
        scheduleClient.doBindService();

		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();
		
		new GregorianCalendar();
		// Création d'un calendrier pour savoir si on peut afficher le drawer
		// (entre le 30 mars et le 2 avril)
		Calendar c = GregorianCalendar.getInstance();
		Calendar lundi = GregorianCalendar.getInstance();
		lundi.set(2015,Calendar.MARCH, 30, 0, 0, 0);
		Calendar jeudi = GregorianCalendar.getInstance();
		jeudi.set(2015,Calendar.APRIL, 02, 0, 0, 0);
		
		// debug
		if (debugMode) c.set(2015, Calendar.MARCH, 30, 0, 0, 59);
		
		treasureDays = c.after(lundi) && c.before(jeudi);
		playDaysAreOver = c.after(jeudi);

		// adding nav drawer items to array
		// ESEO'mega
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Equipe
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Programme
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Sponsors
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Animations
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// Défis
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		// Jeu de piste : du lundi au jeudi
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		// Media / social
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		// A propos
		//navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
		
		// gestion des préférences
		SharedPreferences prefs_Read = getSharedPreferences(PREFS_ID, 0);
        final SharedPreferences.Editor prefs_Write = prefs_Read.edit();
        
        
		
		// On demande si l'utilisateur veut noter l'application sur le Play Store après 5 lancements
		int nbStarts = prefs_Read.getInt("nbStarts", 0);
		if (nbStarts == 4) {
			
			prefs_Write.putInt("nbStarts", nbStarts+1); // if 5, never go here
			prefs_Write.commit();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Vous semblez aimer notre application. Voulez vous la noter sur le Play Store ?")
			       .setTitle("Hey !");
			builder.setCancelable(false);
			builder.setPositiveButton("Oui !", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					// Go to Play Store
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
		         			Uri.parse("https://play.google.com/store/apps/details?id=fr.bde_eseo.eseomega"));
	         				startActivity(intent);
					
				}
				
			});
			
			builder.setNegativeButton("Non, merci", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {} // nothing's here
				
			});

			AlertDialog dialog = builder.create();
			dialog.show();
			
		} else {
			prefs_Write.putInt("nbStarts", nbStarts+1);
			prefs_Write.commit();
		}
		
		// Affichage du SlideDrawer si c'est le premier lancement de l'application
        boolean firstStart = prefs_Read.getBoolean("firstStart", true); // false is when the app has already been started
        if (firstStart) {
	        Toast.makeText(this, "Bienvenue, c'est la première fois que vous démarrez l'application.\nLe bandeau du menu a été ouvert.", Toast.LENGTH_LONG).show();
			mDrawerLayout.openDrawer(mDrawerList);
			prefs_Write.putBoolean("firstStart", false); // now it's not the first app's start
			prefs_Write.commit();
        }
        
        /**
         * Checks if update is available
         */
		
		// App is update : prepare flag for next update
  		if (versionApp.equalsIgnoreCase(serverVersionApp)) {
  			prefs_Write.putBoolean("dontWantToUpdate", false); 
  			prefs_Write.commit();
  		}
         
         // Si il a une mise à jour disponible, on demande à l'utilisateur s'il veut se rendre sur le Play Store
         boolean dontWantToUpdate = prefs_Read.getBoolean("dontWantToUpdate", false); // permet de conserver le choix utilisateur pour une seule différence de version
         
         if (!versionApp.equalsIgnoreCase(serverVersionApp) && !dontWantToUpdate) {
         	
         	//Toast.makeText(this, "Version appli : " + versionApp + ", play store : " + serverVersionApp + ".", Toast.LENGTH_SHORT).show();
 			
 			AlertDialog.Builder builder = new AlertDialog.Builder(this);
 			builder.setMessage("Une mise à jour de cette application est disponible sur le Play Store. Voulez vous la télécharger ?")
 			       .setTitle("Mise à jour disponible");
 			
 			builder.setPositiveButton("Oui !", new DialogInterface.OnClickListener() {
 				@Override
 				public void onClick(DialogInterface arg0, int arg1) {
 					
 					// Go to Play Store
 					Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
 		         			Uri.parse("https://play.google.com/store/apps/details?id=fr.bde_eseo.eseomega"));
 	         				startActivity(intent);
 					
 				}
 				
 			});
 			
 			builder.setNegativeButton("Non, merci", new DialogInterface.OnClickListener() {

 				@Override
 				public void onClick(DialogInterface arg0, int arg1) { // keep in mind that he doesn't want to update its app
 					
 					prefs_Write.putBoolean("dontWantToUpdate", true);
 					prefs_Write.commit();
 					
 				}
 			});

 			builder.setCancelable(false);
 			AlertDialog dialog = builder.create();
 			dialog.show();
         }
         
         // Si intent reçu provenant d'une notification, alors on ouvre ou non le drawer
         if (notifType == 1) {
        	 displayView(4);
         }
         
        // Création d'une alarme / notification fixe pour le 31/03/2015 à 11h55
     	// Create a new calendar set to the date chosen
     	// we set the time to midnight (i.e. the first minute of that day)
 		final Handler handler = new Handler();
 	    handler.postDelayed(new Runnable() {
 	      @Override
 	      public void run() {
 	        //Do something after 100ms
 	    	//setAlarmIntent();
 	      }
 	    }, 500);
 		
        
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onStop() {
    	// When our activity is stopped ensure we also stop the connection to the service
    	// this stops us leaking our activity into the system *bad*
    	if(scheduleClient != null) {
    		scheduleClient.doUnbindService();
    	}
    	
    	super.onStop();
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList); // not used
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		Fragment fragment = null;
		
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			break;
		case 1:
			fragment = new TeamFragment();
			break;
		case 2:
			fragment = new ProgrammeFragment();
			break;
		case 3:
			fragment = new SponsorsFragment();
			break;
		case 4:
			fragment = new AnimationsFragment();
			break;
		case 5:
			fragment = new ChallengeFragment();
			break;
		case 6:
			fragment = new TreasureMenu(!treasureDays, playDaysAreOver);
			break;
		case 7:
			fragment = new MediaSocialFragment();
			break;
		/*case 7:
			fragment = new AboutInfoFragment();
			break;*/

		default:
			break;
		}

		if (fragment != null) {
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.frame_container, fragment).commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("ESEOmega dev", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	

	//function to verify if directory exists
    public static void checkAndCreateDirectory(String dirName){
        File new_dir = new File( Environment.getExternalStorageDirectory() + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
    }
    
    //function to resize a picture
    public static void resizePicture(String name) throws IOException{
    	Bitmap bmp = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/.ESEO'mega/cache/sponsors/" + name);
    	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    	
    	if (bmp != null) { 
        	Bitmap resized; // New bitmap;
        	
        	// Size w and h
        	int w = bmp.getWidth();
        	int h = bmp.getHeight();
        	
        	//Log.d("BMP", "W = " + w + ", H = " + h);
        	
        	// Crop W or H ?
        	if (w > h)
        		resized = Bitmap.createBitmap(bmp, (w-h)/2, 0, w-(w-h), h);
        	else
        		resized = Bitmap.createBitmap(bmp, 0, (h-w)/2, w, h-(h-w));
        	
        	resized.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        	//you can create a new file name "test.jpg" in sdcard folder.
            File f = new File(Environment.getExternalStorageDirectory().toString() + "/.ESEO'mega/cache/sponsors/small/" + name);
            f.createNewFile();
            //write the bytes in file
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            
            // delete old bitmap
            resized.recycle();
            bmp.recycle();

            // remember close FileOutput
            fo.close();
        }
    }
    
    public void setAlarmIntent() {
    	// Create a new calendar set to the date chosen
    	// we set the time to midnight (i.e. the first minute of that day)
    	Calendar c = Calendar.getInstance();
    	/*c.set(2015, Calendar.MARCH, 25);
    	c.set(Calendar.HOUR_OF_DAY, 1);
    	c.set(Calendar.MINUTE, 5);
    	c.set(Calendar.SECOND, 0);*/
    	c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 20);
    	// Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
    	scheduleClient.setAlarmForNotification(c);
    	// Notify the user what they just did
    	//Toast.makeText(this, "Test ok notif", Toast.LENGTH_LONG).show();
    }
    
    public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
