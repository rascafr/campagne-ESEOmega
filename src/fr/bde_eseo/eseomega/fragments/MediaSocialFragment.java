
package fr.bde_eseo.eseomega.fragments;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.asynctasks.SimplePostAsyncTask;
import android.R.color;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
	private String youtubeVidID = "";
	
	private MyWebChromeClient mWebChromeClient = null;
	private View mCustomView;
	private RelativeLayout mContentView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	private LayoutInflater inflater;
	private View rootView;
	private WebView myWebView;
	
	public MediaSocialFragment(){
		
	}
	
	// Ok for moving into other fragments
	@Override
	public void onPause() {
	    super.onPause();
	    myWebView.onPause();
	    myWebView.reload();
	}
	
	// Ok for quit app
	@Override
	public void onStop() {
		super.onStop();
		myWebView.onPause();
	    myWebView.reload();
	    myWebView.clearCache(true);
	};
	
	
	@Override
    public View onCreateView(LayoutInflater inflaterR, ViewGroup container,
            Bundle savedInstanceState) {
 
		inflater = inflaterR;
        rootView = inflater.inflate(R.layout.fragment_mediasocial, container, false);
         
        // Try to fetch the YouTube link, if network available
        if (isOnline()) {
        
	        SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
	        async.execute(shortcutYoutube);
	        try {
	        	youtubeVidID = async.get();
			} catch (InterruptedException e) {} catch (ExecutionException e) {}
        }
        
        //Toast.makeText(getActivity(), "Video link : " + youtubeVideoLink, Toast.LENGTH_SHORT).show();
        
        // Load webview
        myWebView = (WebView) rootView.findViewById(R.id.vidWebView);
        mWebChromeClient = new MyWebChromeClient();
        myWebView.setWebChromeClient(mWebChromeClient);
        WebSettings ws = myWebView.getSettings();
        ws.setJavaScriptEnabled(true);
        myWebView.setOnLongClickListener(null);
        
        // Replace "[]" by the video iD
        // Get the size of screen :
        // Formulas : 100% width, height = 100% / (16/9)
        
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
        Log.d("Size", "Screen : " + width + "x" + height);
        // width > height
        // more the screen is square, more ratio1 -> 1, so more we have to reduce height
        double ratio2 = (9.0/16.0) * ratio1 * 100;
        int ratio2int = (int) ratio2 + 2;
        ratio2int = (int) (60-5*(1/ratio1));
        ratio2int = (int) (56.0 + (0.58-ratio1)*8.0);
        
        
        //debug 
        //youtubeVidID = "A3PDXmYoF5U";
        String videoLink = "<p><iframe width=\"100%\" height=\"" + ratio2int + "%\" src=\"https://www.youtube.com/embed/" + youtubeVidID + "\" frameborder=\"0\" allowfullscreen></iframe></p>";
        
        String imgLink = "<img src = \"file:///android_asset/mediasocial/video_no_network.jpg\" width=\"100%\" height=\"" + ratio2int + "%\">";
        
        if (isOnline()) { // if online, load video
        	myWebView.loadData(videoLink, "text/html", "utf-8");        	
        	
        } else // else, load image with message
        	myWebView.loadDataWithBaseURL("file:///android_asset/", imgLink, "text/html", "utf-8", null);
        

        
        /**
         * Définition des liens pour les réseaux sociaux
         */
        
        // Snap-chat
        ImageView imgSnapchat = (ImageView) rootView.findViewById(R.id.mediaImgSnap);
        imgSnapchat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Snapchat");
				
				View vDialog = inflater.inflate(R.layout.custom_dialog_print_snapaccount, null);
				
				builder.setView(vDialog);
				
				builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}										
				});
				builder.show();
			}
		});
        
        // Facebook
        ImageView imgFacebook = (ImageView) rootView.findViewById(R.id.mediaImgFacebook);
        imgFacebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(facebookIntent));
				if (intent.resolveActivity(getActivity().getPackageManager()) != null)
					startActivity(intent);	
				else {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookPage));
	        		startActivity(browserIntent);
				}		
			}
		});
        
        // Twitter
        ImageView imgTwitter = (ImageView) rootView.findViewById(R.id.mediaImgTwitter);
        imgTwitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(twitterIntent));
				if (intent.resolveActivity(getActivity().getPackageManager()) != null)
					startActivity(intent);	
				else {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterPage));
	        		startActivity(browserIntent);
				}
			}
		});
        
		rootView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if( keyCode == KeyEvent.KEYCODE_BACK ) {  
					myWebView.clearCache(true);
					myWebView.reload();
		        }
				return false;
			}
		});
        
        // Assignation des données dans la WebView
    	//myWebView.load(mContent, "text/html; charset=utf-8", "UTF-8");
        //myWebView.loadDataWithBaseURL( "file:///android_asset/", videoLink, "text/html", "utf-8", null ); 
        //Toast.makeText(getActivity(), "Video : " + videoLink, Toast.LENGTH_LONG).show();
        //myWebView.loadData(videoLink, "text/html", "utf-8");
        
        return rootView;
    }
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
	
	 
	
	private class MyWebChromeClient extends WebChromeClient {
	    FrameLayout.LayoutParams layoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
	            FrameLayout.LayoutParams.MATCH_PARENT);
	

	    @Override
	    public void onShowCustomView(View vieww, CustomViewCallback callback) {
	        // if a view already exists then immediately terminate the new one
	        if (mCustomView != null) {
	            callback.onCustomViewHidden();
	            return;
	        }
	        
	        Toast.makeText(getActivity(), "Vous allez être redirigés vers l'application Youtube.", Toast.LENGTH_SHORT).show();	
        	myWebView.clearCache(true);
        	myWebView.reload();
        	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + youtubeVidID));
        	
			if (intent.resolveActivity(getActivity().getPackageManager()) != null)
				startActivity(intent);	
			else {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.youtube.com/watch?v=" + youtubeVidID));
        		startActivity(browserIntent);
			}
	        
	    }

	    @Override
	    public void onHideCustomView() {
	        if (mCustomView == null) {
	            return;
	        } else {
	            // Hide the custom view.  
	            mCustomView.setVisibility(View.GONE);
	            // Remove the custom view from its container.  
	            mCustomViewContainer.removeView(mCustomView);
	            mCustomView = null;
	            mCustomViewContainer.setVisibility(View.GONE);
	            mCustomViewCallback.onCustomViewHidden();
	            // Show the content view.  
	            mContentView.setVisibility(View.VISIBLE);
	            getActivity().setContentView(mContentView);
	        }
	    }
	}
}
