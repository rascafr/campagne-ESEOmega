
package fr.bde_eseo.eseomega.youtubepackage;
import java.util.concurrent.ExecutionException;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.asynctasks.SimplePostAsyncTask;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Page youtube / snap / facebook.
 * @author François
 *
 */
public class MediaSocialFragment extends Fragment {
	
	private String facebookPage = "https://www.facebook.com/eseomega";
	private String facebookIntent = "fb://page/806769696065152";
	private String twitterPage = "https://twitter.com/bde_eseomega";
	private String twitterIntent = "twitter://user?screen_name=bde_eseomega";
	private String shortcutYoutube = "http://176.32.230.7/eseomega.com/video.txt";
	
	private View mCustomView;
	private RelativeLayout mContentView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private LayoutInflater inflater;
	private View rootView;
	
	
	public MediaSocialFragment(){}
	
	private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private String videoID = "";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);
        
        // Try to fetch the YouTube link, if network available
        if (isOnline()) {
        
	        SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
	        async.execute(shortcutYoutube);
	        try {
	        	videoID = async.get();
			} catch (InterruptedException e) {videoID = "";} catch (ExecutionException e) {videoID = "";}
        }

        // Save the web view
        webView = (VideoEnabledWebView) rootView.findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = rootView.findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) rootView.findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, webView) // See all available constructors...
        {
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                // Your code...
            }
        };
        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
  
            	
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen)
                {
                	//getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                	/*WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                    	getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }*/
                	Toast.makeText(getActivity(), "Vous allez être redirigés vers l'application Youtube.", Toast.LENGTH_SHORT).show();	
                	webView.clearCache(true);
                	webView.reload();
                	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoID));
                	
    				if (intent.resolveActivity(getActivity().getPackageManager()) != null)
    					startActivity(intent);	
    				else {
    					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.youtube.com/watch?v=" + videoID));
    	        		startActivity(browserIntent);
    				}	
                }
                else
                {
                	webView.clearCache(true);
                	webView.reload();
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);
        
        // Get size of screen
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        
        // We want to calculate the height in % to have a 16/9 video
        // 100% by 100% means a full screen vertical video
        // So a video in ratio width / height
        
        // We have ratio1 = w/h = 100% / 100%
        // we want ratio2 = w/hh = 100% / x = 16/9
        double ratio1 = ((double)width/height);
        double ratio2 = (9.0/16.0) * ratio1 * 100;
        int ratio2int = (int) ratio2 + 2;
        
        
        String videoLink = "<iframe width=\"100%\" height=\"" + ratio2int + "%\" src=\"https://www.youtube.com/embed/" + videoID + "\" frameborder=\"0\" allowfullscreen></iframe>";
	    
        String imgLink = "<img src = \"file:///android_asset/mediasocial/video_no_network.jpg\" width=\"100%\" height=\"" + ratio2int + "%\">";
        
        Log.d("Video", "Width = " + webView.getWidth());
        
        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        //webView.loadUrl("http://m.youtube.com/watch?v=v_qPcAvB3Pk");
        if (isOnline() && videoID.length() > 0) { // if online, load video
    	    webView.loadData(videoLink, "text/html", "utf-8");
    	} else // else, load image with message
        	webView.loadDataWithBaseURL("file:///android_asset/", imgLink, "text/html", "utf-8", null); 
		return rootView;

    }

    /*@Override
    public void onBackPressed()
    {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!webChromeClient.onBackPressed())
        {
            if (webView.canGoBack())
            {
                webView.goBack();
            }
            else
            {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }*/
    
    public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}

}
