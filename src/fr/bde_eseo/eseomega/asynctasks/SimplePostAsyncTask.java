package fr.bde_eseo.eseomega.asynctasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.AsyncTask;

//usually, subclasses of AsyncTask are declared inside the activity class.
//that way, you can easily modify the UI thread from here
public class SimplePostAsyncTask extends AsyncTask<String, Integer, String> {

	private Context context;
	
	public SimplePostAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... sUrl) {
		
		URL url = null;
		String result = null;
		
		try { url = new URL(sUrl[0]); } catch (MalformedURLException e) {};
		
		URLConnection urlConnection;
		try { 
			urlConnection = url.openConnection(); 
			InputStream is = urlConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			String read = br.readLine();
			
			while (read != null) {
				sb.append(read);
				read = br.readLine();
			}
			
			result = sb.toString();
			
		} catch (IOException e) {}
		
		return result;
	}
}