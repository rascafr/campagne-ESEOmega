package fr.bde_eseo.eseomega;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import fr.bde_eseo.eseomega.asynctasks.SimplePostAsyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashScreen extends Activity {

	// Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private String versionApp;
    private String serverVersion;
    private RelativeLayout layProgress;
    private TextView tvInfo;
    private Handler mHandler;
    private int countAnim = 0;
    private int total = 0;
	private int count = 0;
	private int width;
	private int height;
	private int middle;
	private double px;
	private static final int MAX_STEPS = 40;
	private static final int ANIM_STEPS = 12;
	private static final int TIMER_DELAY = 50;
    private static String serverVersionLink = "http://176.32.230.7/eseomega.com/appAndroidVersion.txt";
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        tvInfo = (TextView) findViewById(R.id.tvSplashInfo);
        layProgress = (RelativeLayout) findViewById(R.id.layProgress);
        layProgress.setVisibility(View.VISIBLE);
        layProgress.setAlpha((float) 0.0);
        
        // Get size of screen
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        middle = width / 2;
        
        // Get this app's version
 		try { versionApp  = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName; }
 		catch (NameNotFoundException e) { versionApp = "0.0"; }
 		
 		tvInfo.setText("Version " + versionApp + " - ESEOmega 2015");
 		
 		px = width / (ANIM_STEPS * 4.5);
 		
 		mHandler = new android.os.Handler();
    	mHandler.postDelayed(updateTimerThread, 0);
 
        /*new Handler().postDelayed(new Runnable() {
 
            @Override
            public void run() {
                // This method will be executed once the timer is over
            	
            	
            }
        }, SPLASH_TIME_OUT);*/
    }
    
    @Override
    public void onBackPressed() {
    	// do nothing
    }
    
    private Runnable updateTimerThread = new Runnable() {
	        public void run() {

	        	if (countAnim > (MAX_STEPS - ANIM_STEPS)) {
	        		
	        		layProgress.setAlpha((float) (0.5+0.01*(countAnim - (MAX_STEPS - ANIM_STEPS))));
		        	layProgress.setLeft((int) (middle-px*(countAnim - (MAX_STEPS - ANIM_STEPS))));
		        	layProgress.setRight((int) (middle+px*(countAnim - (MAX_STEPS - ANIM_STEPS))));
	        	}
	        	
	        	mHandler.postDelayed(this, TIMER_DELAY);
	        	countAnim++;
	        	
	        	if (countAnim >= MAX_STEPS) {
	        		new PrefetchData().execute();
	        		countAnim = 0;
	        	}
	        }
	};
 		
	/**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHandler.removeCallbacks(updateTimerThread);
            // before making http calls   
        }
 
        @Override
        protected Void doInBackground(Void... sUrl) {
            /*
             * Will make http call here 
             */
        	URL url = null;
        	int size = 0;
        	
        	if (isOnline()) {
    		
	    		try { url = new URL(serverVersionLink); } catch (MalformedURLException e) {};
	    		
	    		URLConnection urlConnection;
	    		
	    		try { 
	    			urlConnection = url.openConnection(); 
	    			size = urlConnection.getContentLength();
	    			InputStream is = urlConnection.getInputStream();
	    			InputStreamReader isr = new InputStreamReader(is);
	    			StringBuilder sb = new StringBuilder();
	    			BufferedReader br = new BufferedReader(isr);
	    			String read = br.readLine();
	    			
	    			
	    			
	    			while (read != null) {
	    				count += read.length();
	    				//layProgress.setLayoutParams(new LayoutParams((count * 100 / size), 2));
	    				sb.append(read);
	    				read = br.readLine();
	    			}
	    			
	    			serverVersion = sb.toString();
	    			
	    		} catch (IOException e) { serverVersion = ""; }
        	} else {
        		serverVersion = "";
        	}
 
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // After completing http call
            // will close this activity and lauch main activity
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            
            // Check results before sending it to MainActivity
            if (serverVersion.indexOf("<") != -1 || serverVersion.indexOf(".") == -1) // HTML tag : wrong page (Hotel, ESEO Wifi, HotSpot ...)
            	serverVersion = versionApp; // no update if no internet / wrong connexion
            
            i.putExtra("versionServer", serverVersion);
            i.putExtra("versionApp", versionApp);
            startActivity(i);
 
            // close this activity
            finish();
        }
 
    }
    
    public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
}
