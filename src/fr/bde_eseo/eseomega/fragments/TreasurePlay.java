package fr.bde_eseo.eseomega.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.asynctasks.SimplePostAsyncTask;
import fr.bde_eseo.eseomega.myObjects.DialogError;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;

/**
 * Page gérant la section de jeu de l'animation Qr Code.
 * @author François
 *
 */
public class TreasurePlay extends Fragment implements OnQRCodeReadListener {
	
	private static final int refreshPointsInterval = 20000;
	private static final int refreshQrSend = 8000;
	private String contentURL = TreasureMenu.scorePHP + "?" + "e=";
	private TextView tvMemberPoints;
	private Handler mHandler;
	private long timestampQr = 0;
	private QRCodeReaderView mydecoderview;
	private SharedPreferences teamNamePrefs_Read;
	private SharedPreferences.Editor teamNamePrefs_Write;
	private LayoutInflater inflater;
	
	// Codes d'erreur : scan d'un Qr Code
	private final static int ERROR_NONE = 0;
	private final static int ERROR_ALREADY_SCAN = 1;
	private final static int ERROR_WRONG_CODE = 2;
	private final static int ERROR_UNKNOWN = -1;
	
	// Gestion des Dialog
	private boolean isDialogOpen = false;
	
	// Constantes des clés de préférence
	public final static String PREFS_KEY_POINTS = "teamPoints";
	
	private String instructions  =  "<br><b>7 QRcodes faciles (2 points) :</b><br>" +
									"- La salle de l’impulsion de…<br>" +
									"- Un peu de sport ! Montez 75 marches.<br>" +
									"- À côté du monte-charge comme dirait Jérôme !<br>" +
									"- La raie du (cin)q.<br>" +
									"- Poséidon, Hadès et Midas en détiennent un chacun.<br><br>" +
								
									"<b>6 QRcodes moyens (4 points) :</b><br>" +
									"- Quand tu veux retirer de l’argent pour aller à la Trinquette.<br>" +
									"- La fontaine à embrouille, heureusement on peut s’y faire pardonner pas très loin.<br>" +
									"- La porte de Zeus.<br>" +
									"- Dionysos se rend régulièrement dans cette rue pour se désaltérer.<br>" +
									"- RU Croix Rouge.<br>" +
									"- L’endroit pour mater et faire semblant de travailler.<br><br>" +
								
									"<b>5 QRcodes pour les dieux (6 points) :</b><br>" +
									"- Place du marché le mardi matin.<br>" +
									"- L’ancien bercail.<br>" +
									"- Sous Confluence.<br>" +
									"- Martin y va tous les ans.<br>" +
									"- La colonne 11A, j’optimisme.<br><br>" +
									
									"<b>+ 2 pts si tous les défis moyens réalisés</b><br>" +
									"<b>+ 4 pts si tous les défis difficiles réalisés</b><br>";
	
	public TreasurePlay(){}
	
	@Override
    public View onCreateView(LayoutInflater inflaterR, ViewGroup container,
            Bundle savedInstanceState) {
		
		// Get parameters from preferences
		teamNamePrefs_Read = getActivity().getSharedPreferences(TreasureMenu.PREFS_NAME_TEAM, 0);
        teamNamePrefs_Write = teamNamePrefs_Read.edit();
        
        inflater = inflaterR;
 
        // Inflate
        View rootView = inflater.inflate(R.layout.fragment_treasure_play, container, false);
        TextView tvTeamName = (TextView) rootView.findViewById(R.id.treasureTeamName);
        TextView tvMemberName = (TextView) rootView.findViewById(R.id.treasureMemberFullName);
        tvMemberPoints = (TextView) rootView.findViewById(R.id.treasureTeamPoints);
        final RelativeLayout layInfo = (RelativeLayout) rootView.findViewById(R.id.layoutTreasureInfo);
        
        // Set parameters into view
        String teamName = teamNamePrefs_Read.getString("teamName", "#erreur");
        String shortedTeam = teamName;
        if (shortedTeam.length() > 20) { 
        	shortedTeam = shortedTeam.substring(0, 20);
        	shortedTeam += "...";
        }
        tvTeamName.setText(shortedTeam);
        
        String memberName = teamNamePrefs_Read.getString("teamUserFirstName", "#erreur") + " " + teamNamePrefs_Read.getString("teamUserName", "#erreur");
        String shortedMember = memberName;
        if (shortedMember.length() > 21) { // +1 : middle space
        	shortedMember = shortedMember.substring(0, 21);
        	shortedMember += "...";
        }
        tvMemberName.setText(shortedMember);
        
        // Get number of points from internet
        // Encode Team Name
        String teamNameEncode = "";
        try { teamNameEncode = URLEncoder.encode(teamName, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
        contentURL += teamNameEncode;
        
        // Post sur le serveur en async
		String teamPoints = "---";
        
        // Check internet connexion
    	if (isOnline()) {
    		SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
    		async.execute(contentURL);
    		try { teamPoints = async.get(); } catch (InterruptedException e) { teamPoints = "---"; } catch (ExecutionException e) { teamPoints = "---"; }
    		
    		// Sync points with preferences if correct page (not on firewall.eseo.fr) -> assuming points are corrects here
    		if (teamPoints.indexOf("<") == -1) { // no <HTML> tag
    			teamNamePrefs_Write.putString(PREFS_KEY_POINTS, teamPoints);
    			teamNamePrefs_Write.commit();
    		} else { // Incorrect string
    			teamPoints = "---"; // set temp string to incorrect to force load from preferences
    		}
    	} 
    	
    	if (teamPoints.equalsIgnoreCase("---")) { // Internet not available / wrong result
    		teamPoints = teamNamePrefs_Read.getString(PREFS_KEY_POINTS, "0");
    	}
        
    	if (teamPoints.length() >= 1) { // team has been deleted
    		tvMemberPoints.setText(teamPoints + " pts");
    	} else {
    		tvMemberPoints.setText("--- pts");
    		tvMemberPoints.setTextColor(Color.rgb(0xAA, 0x10, 0x10));
    		DialogError dialErr = new DialogError("Désolé ...", "Votre équipe n'existe plus. Si vous avez tenté de tricher ou d'accéder à des ressources non publiques, c'est normal.\nSinon, contactez-nous.", getActivity());
			dialErr.openDialog();
    	}
    	
    	// On clic on about
    	layInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				isDialogOpen = true;
				
				//layInfo.setBackgroundColor(Color.DKGRAY);
			
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder//.setMessage(instructions)
				       .setTitle("Instructions de jeu");
				
				View vDialog = inflater.inflate(R.layout.custom_layout_text, null);
				
				builder.setView(vDialog);
				
				TextView tvTxt = (TextView) vDialog.findViewById(R.id.txt);
				tvTxt.setText(Html.fromHtml(instructions));
				
				builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						isDialogOpen = false;
					}										
				});
				
				builder.show();
				
			}
		});
    	
    	// Delay to update data
    	mHandler = new android.os.Handler();
    	mHandler.postDelayed(updateTimerThread, 0);
    	
    	// Start QR Code listener
    	mydecoderview = (QRCodeReaderView) rootView.findViewById(R.id.qRCodeReaderView);
        mydecoderview.setOnQRCodeReadListener(this);
    	
    	return rootView;
	}
	
	private Runnable updateTimerThread = new Runnable()
	{
	        public void run()
	        {
	        	// check presence
	        	
	        	boolean boolIsOnline = false;
	        	
	        	try { 
	        		boolIsOnline = isOnline();
	        		
	        		// Repeat internet connexion
		        	String teamPoints = "---";
		        	if (boolIsOnline) {
		        		SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
		        		async.execute(contentURL);
		        		try { teamPoints = async.get(); } catch (InterruptedException e) { teamPoints = "---"; } catch (ExecutionException e) { teamPoints = "---"; }     
		        		
		        		// Sync points with preferences if correct page (not on firewall.eseo.fr) -> assuming points are corrects here
			    		if (teamPoints.indexOf("<") == -1) { // no <HTML> tag
			    			teamNamePrefs_Write.putString(PREFS_KEY_POINTS, teamPoints);
			    			teamNamePrefs_Write.commit();
			    		} else { // Incorrect string
			    			teamPoints = "---"; // set temp string to incorrect to force load from preferences
			    		}
			    		
			    		if (teamPoints.equalsIgnoreCase("---")) { // Internet not available / wrong result
				    		teamPoints = teamNamePrefs_Read.getString(PREFS_KEY_POINTS, "0");
				    	}
		        		
		        		tvMemberPoints.setText(teamPoints + " pts");
			        	
		        	}		    	
		        	
		        	mHandler.postDelayed(this, refreshPointsInterval);
	        		
	        	} catch (NullPointerException e) {
	        		Log.d("Hand", "Handler stopped"); boolIsOnline = false; mHandler.removeCallbacks(updateTimerThread);
	        	}
	        	
		            
	        	
	        }
	};
	
	// Inner Runnable which can be posted to the handler
    class QuitLooper implements Runnable
    {
        @Override
        public void run() 
        {
            Looper.myLooper().quit();
        }
    }
	
	
	
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	@Override
	public void onQRCodeRead(String text, PointF[] points) {
		
		Calendar c = Calendar.getInstance(Locale.FRANCE);
		
		Log.d("bool", "" + isDialogOpen);
		
		// Wait for at least 10 seconds between two scans to avoid multiples requests
		if ((c.getTimeInMillis() - timestampQr > refreshQrSend) && !isDialogOpen){
			
			
			if (isOnline()) {
				
				String qrCodeEncode = "";
				String serverValidation = "-1";
				String teamNameEncode = "";
				
				// Encore Qr Code
				try { qrCodeEncode = URLEncoder.encode(text, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
				
				// Encore team name
				SharedPreferences teamNamePrefs_Read = getActivity().getSharedPreferences(TreasureMenu.PREFS_NAME_TEAM, 0);
		        String teamName = teamNamePrefs_Read.getString("teamName", "#erreur");
		        try { teamNameEncode = URLEncoder.encode(teamName, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
				
				// Send the code online
				SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
				async.execute(TreasureMenu.qrPHP + "?e=" + teamNameEncode + "&c=" + qrCodeEncode);
				try { serverValidation = async.get(); } catch (InterruptedException e) { serverValidation = "-1"; } catch (ExecutionException e) { serverValidation = "-1"; }
				
				// Monitor errors
				int serverValidationInt;
				try { serverValidationInt = Integer.parseInt(serverValidation); }  catch (NumberFormatException e) { serverValidationInt = ERROR_UNKNOWN; }
				
				
				DialogError dialErr;
				
				switch (serverValidationInt) {
				
				case ERROR_ALREADY_SCAN:
					dialErr = new DialogError("Oups !", "Ce Qr Code a déjà été scanné par votre équipe !", getActivity());
					dialErr.openDialog();
					break;
					
				case ERROR_UNKNOWN:
					dialErr = new DialogError("Erreur inconnue", "Une erreur inconnue s'est produite. Veuillez réessayer ...", getActivity());
					dialErr.openDialog();
					break;
					
				case ERROR_WRONG_CODE:
					dialErr = new DialogError("Erreur utilisateur", "Dommage, ce Qr Code n'est pas valide ! (Tentez vous de tricher ?)", getActivity());
					dialErr.openDialog();
					break;
					
				case ERROR_NONE:
					
					// Display response
					isDialogOpen = true;
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setMessage("Vous avez trouvé un Qr Code !\nContinuez ainsi !")
					       .setTitle("Féliciations !");
					
					builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							isDialogOpen = false;
						}										
					});
					
					builder.show();
					
					// Update point counter
					String teamPoints = "---";
					SimplePostAsyncTask asyncPoints = new SimplePostAsyncTask(getActivity());
					asyncPoints.execute(contentURL);
	        		try { teamPoints = asyncPoints.get(); } catch (InterruptedException e) { teamPoints = "---"; } catch (ExecutionException e) { teamPoints = "---"; }    
	        		
	        		// Sync points with preferences if correct page (not on firewall.eseo.fr) -> assuming points are corrects here
		    		if (teamPoints.indexOf("<") == -1) { // no <HTML> tag
		    			teamNamePrefs_Write.putString(PREFS_KEY_POINTS, teamPoints);
		    			teamNamePrefs_Write.commit();
		    		} else { // Incorrect string
		    			teamPoints = "---"; // set temp string to incorrect to force load from preferences
		    		}
		    		
		    		if (teamPoints.equalsIgnoreCase("---")) { // Wrong result
		    			teamPoints = teamNamePrefs_Read.getString(PREFS_KEY_POINTS, "0");
			    	}
	        		
	        		tvMemberPoints.setText(teamPoints + " pts");
					
					break;
					
				}
				
			} else {
				DialogError dialErr = new DialogError("Erreur utilisateur", "L'application doit se connecter à Internet, or les connexions de données semblent être désactivées. Veuillez vérifier vos paramètres réseau.", getActivity());
				dialErr.openDialog();
			}
			
			timestampQr = c.getTimeInMillis();
		}
	}

	@Override
	public void cameraNotFound() {
		DialogError dialErr = new DialogError("Désolé ...", "Votre appareil ne dispose pas d'une caméra, ou elle n'est pas reconnue.", getActivity());
		dialErr.openDialog();		
	}

	@Override
	public void QRCodeNotFoundOnCamImage() {
		// Nothing to do here	
	}
}
