package fr.bde_eseo.eseomega.youtubepackage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;

import fr.bde_eseo.eseomega.R;

public class FragmentYoutube extends Fragment
{

public FragmentYoutube () {}
	
    private VideoEnabledWebView webView;
    private VideoEnabledWebChromeClient webChromeClient;
    private String videoID = "";
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_youtube, container, false);
        
        videoID = "v_qPcAvB3Pk";

        // Save the web view
        webView = (VideoEnabledWebView) rootView.findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = rootView.findViewById(R.id.nonVideoLayout); // Your own view, read class comments
        ViewGroup videoLayout = (ViewGroup) rootView.findViewById(R.id.videoLayout); // Your own view, read class comments
        //noinspection all
        View loadingView = inflater.inflate(R.layout.view_loading_video, null); // Your own view, read class comments
        webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See all available constructors...
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
                    /*WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                    	getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }*/
                	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + videoID));
                	
    				if (intent.resolveActivity(getActivity().getPackageManager()) != null)
    					startActivity(intent);	
    				else {
    					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.youtube.com/" + videoID));
    	        		startActivity(browserIntent);
    				}	
                }
                else
                {
                    WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    getActivity().getWindow().setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        //noinspection all
                    	getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }

            }
        });
        webView.setWebChromeClient(webChromeClient);

        // Navigate anywhere you want, but consider that this classes have only been tested on YouTube's mobile site
        //webView.loadUrl("http://m.youtube.com/watch?v=v_qPcAvB3Pk");
        String videoLink = "<iframe width=\"100%\" height=\"60%\" src=\"https://www.youtube.com/embed/v_qPcAvB3Pk\" frameborder=\"0\" allowfullscreen></iframe>";
        webView.loadData(videoLink, "text/html", "utf-8");
        
        rootView.setOnKeyListener(new OnKeyListener() {
			
        	@Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if( keyCode == KeyEvent.KEYCODE_BACK ){
                	
                	// Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
                    if (!webChromeClient.onBackPressed()) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                        else {
                            
                        }
                    }
                	
                    
                }
                return false;
        	}
        
        });
        
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

}
