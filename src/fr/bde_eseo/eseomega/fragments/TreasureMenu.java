package fr.bde_eseo.eseomega.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.asynctasks.SimplePostAsyncTask;
import fr.bde_eseo.eseomega.myObjects.DialogError;
import fr.bde_eseo.eseomega.utils.Utilities;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Page pour l'animation du jeu de la chasse au trésor avec les QR Codes placés  à différents endroits de la ville.
 * @author François
 *
 */
public class TreasureMenu extends Fragment  {
	
	private boolean playDaysAreOver;
	private boolean showPause;

public TreasureMenu(boolean showPause, boolean playDaysAreOver){
	
	this.showPause = showPause;
	this.playDaysAreOver = playDaysAreOver;
}

	// Préférences
	public static String PREFS_NAME_TEAM = "mPrefsRegister";
	
	// Paramètres serveur
	public final static String serverURL = "http://176.32.230.7/eseomega.com/";
	public final static String folderURL = "qr/";
	public final static String createPHP = serverURL + folderURL + "createteam.php";
	public final static String joinPHP = serverURL + folderURL + "jointeam.php";
	public final static String qrPHP = serverURL + folderURL + "scanqr.php"; //e=equipe&c
	public final static String scorePHP = serverURL + folderURL + "scoreteam.php"; // ?e=Les%20Simpson
	public final static String teamNamePHP = serverURL + folderURL + "getmyteam.php";
	public final static String getchiefPHP = serverURL + folderURL + "getchief.php"; // ?e=lesandroid
	
	// Erreurs possibles
	private final static int ERROR_NONE = 0;
	private final static int ERROR_UNKNOWN = -1;
	
	// Création
	private final static int ERROR_CREATE_EXIST_TEAM = 1;
	private final static int ERROR_CREATE_ALREADY_OTHER_TEAM = 2;
	
	private final static int ERROR_JOIN_INEXIST_TEAM = 1;
	private final static int ERROR_JOIN_ALREADY_HERE = 2;
	private final static int ERROR_JOIN_FULL_TEAM = 4;
	private final static int ERROR_JOIN_ALREADY_OTHER_TEAM = 8;
	
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		       
		
		View rootView = inflater.inflate(R.layout.fragment_treasure_menu, container, false);
        Button buttonNew = (Button) rootView.findViewById(R.id.buttonNew);
        Button buttonExists = (Button) rootView.findViewById(R.id.buttonExists);
        
        // SI EQUIPE DEJA ENREGISTREE, on vire tout vers TreasurePlay / Ou TreasurePause
        
        // Préférences
        SharedPreferences teamNamePrefs_Read = getActivity().getSharedPreferences(PREFS_NAME_TEAM, 0);
        final SharedPreferences.Editor teamNamePrefs_Write = teamNamePrefs_Read.edit();
        
        boolean teamHasBeenRegistered = teamNamePrefs_Read.getBoolean("isInTeam", false);
        
        if (showPause) {

        	Fragment fragment = new TreasurePause(playDaysAreOver);
			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();

			}
        	
        } else if (!teamHasBeenRegistered) {            
        
	        // Listener on New Team button
	        buttonNew.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					/**
					 * Création d'une équipe sur le serveur.
					 * Nom, Prénom et Nom de l'équipe
					 * Utilisation du lien createPHP.
					 * 
					 * Sources : Thomas.
					 * 
					 *   CRÉATION D'ÉQUIPE
						  0x0 : pas d’erreur et l’équipe a été créée via PHP depuis le serveur
						  0x1 : nom d’équipe déjà existant
						  0x2 : nom du chef d’équipe déjà présent dans une équipe
						  
						  GET
						  e = nom d'équipe
						  n = nom du chef
						  p = prénom du chef
					 */
					
					final Dialog dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.custom_dialog_treasure_new);
					dialog.setTitle("Création d'une équipe");
		 
					// Création -> Bouton OK
					Button btOk = (Button) dialog.findViewById(R.id.buttonNewTeamOk);
					btOk.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							
							// Variable pour la récupération PHP
					        String createPHPresult = "";
							
							// Récupération depuis les EditText
							EditText teamNameEdit = (EditText) dialog.findViewById(R.id.editTextNewTeamName);
							EditText chiefNameEdit = (EditText) dialog.findViewById(R.id.editTextNewTeamChiefName);
							EditText chiefFirstEdit = (EditText) dialog.findViewById(R.id.editTextNewTeamChiefFirstName);
							
							// Trim des espaces
							String teamNameStr = Utilities.trimSpaces(teamNameEdit.getText().toString());
							String chiefNameStr = Utilities.trimSpaces(chiefNameEdit.getText().toString());
							String chiefFirstStr = Utilities.trimSpaces(chiefFirstEdit.getText().toString());
							
							// Conversion en UTF
							String utf8teamNameStr = "", utf8chiefNameStr = "", utf8chiefFirstStr = "";
							try { utf8teamNameStr = URLEncoder.encode(teamNameStr, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
							try { utf8chiefNameStr = URLEncoder.encode(chiefNameStr, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
							try { utf8chiefFirstStr = URLEncoder.encode(chiefFirstStr, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
							
							// Check internet connexion
							if (!isOnline()) {
								DialogError dialErr = new DialogError("Erreur utilisateur", "L'application doit se connecter à Internet, or les connexions de données semblent être désactivées. Veuillez vérifier vos paramètres réseau.", getActivity());
								dialErr.openDialog();
							} 
							// Check values
							else if (teamNameStr.length() == 0 || chiefNameStr.length() == 0 || chiefFirstStr.length() == 0) {
								DialogError dialErr = new DialogError("Erreur utilisateur", "Impossible de créer l'équipe ! Veuillez vérifier que vous avez correctement complété les informations demandées.", getActivity());
								dialErr.openDialog();
							}
							// Everything's seems okay, let's send it to the server
							else {
							
								// Post sur le serveur	en async
								String contentURL = createPHP + "?" + "e=" + utf8teamNameStr + "&n=" + utf8chiefNameStr + "&p=" + utf8chiefFirstStr;
								SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
								async.execute(contentURL);
								try { createPHPresult = async.get(); } catch (InterruptedException e) {} catch (ExecutionException e) {}
								
								// Now, we have the result, decide what to do with.
								if (createPHPresult == null) {
									
									DialogError dialErr = new DialogError("Erreur serveur", "Impossible de créer l'équipe : notre serveur est sans doute hors-service. Patientez un peu, nous sommes déjà sur le coup !", getActivity());
									dialErr.openDialog();
									
								} else {
									
									// convert String to Int (try it)
									int resultPHP;
									DialogError dialErr;
									
									try { resultPHP = Integer.parseInt(createPHPresult); }  catch (NumberFormatException e) { resultPHP = ERROR_UNKNOWN; }
									
									switch (resultPHP) {
									
									case ERROR_UNKNOWN:
										dialErr = new DialogError("Erreur inconnue", "Impossible de créer l'équipe : raison inconnue. Merci de signaler le code d'erreur. (0x" + createPHPresult + ")", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_CREATE_EXIST_TEAM:
									case ERROR_CREATE_EXIST_TEAM | ERROR_CREATE_ALREADY_OTHER_TEAM:
										dialErr = new DialogError("Erreur utilisateur", "Impossible de créer l'équipe, car celle-ci existe déjà ! Veuillez saisir un autre nom.", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_CREATE_ALREADY_OTHER_TEAM:
										dialErr = new DialogError("Erreur utilisateur", "Impossible de créer l'équipe, car vous êtes déjà inscrit dans une autre équipe ! (Tentez vous de tricher ?)", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_NONE:
										
										// Save Name, and TeamName in preferences
										teamNamePrefs_Write.putBoolean("teamIsChief", true); // it's the chief
										teamNamePrefs_Write.putString("teamName", teamNameStr);
										teamNamePrefs_Write.putString("teamUserFirstName", chiefFirstStr);
										teamNamePrefs_Write.putString("teamUserName", chiefNameStr);
										teamNamePrefs_Write.putBoolean("isInTeam", true);
										teamNamePrefs_Write.commit();
										
										AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
										builder.setMessage("Bienvenue " + chiefFirstStr + ", vous venez de créer l'équipe \"" + teamNameStr + "\" !\n\nBonne chance !")
										       .setTitle("Hey !");
										builder.setCancelable(false);
										builder.setPositiveButton("Commencer à jouer", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface arg0, int arg1) {
												
												Fragment fragment = new TreasurePlay();
												if (fragment != null) {
													FragmentManager fragmentManager = getFragmentManager();
													fragmentManager.beginTransaction()
															.replace(R.id.frame_container, fragment).commit();
			
												}
												
											}										
										});
										
										builder.show();
										break;
									
									}
								}
								
								dialog.dismiss();
							}
						}
					});
					
					Button btCancel = (Button) dialog.findViewById(R.id.buttonNewTeamCancel);
					btCancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		 
					dialog.show();				
				}
			});
	        
	        // Listener on Rejoin Team button
	        buttonExists.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					/**
					 * Rejoindre une équipe : jointeam.php
					 *	- 0x0 : pas d’erreur, et la personne a rejoint l’équipe, PHP a enregistré son nom dans la base de données mySQL.
					 *	- 0x1 : l’équipe que la personne tente de rejoindre n’existe pas
					 *	- 0x2 : le participant est déjà présent dans l’équipe
					 *	- 0x4 : l’équipe est pleine (n=4)
					 *	- 0x8 : la personne qui tente de rejoindre l’équipe est déjà présente dans une autre team
					 *
					 *	GET
						  e = nom d'équipe
						  n = nom du chef
						  p = prénom du chef
					 *
					 */
					
					final Dialog dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.custom_dialog_treasure_join);
					dialog.setTitle("Rejoindre une équipe");
		 
					// Rejoindre -> Bouton OK
					Button btOk = (Button) dialog.findViewById(R.id.buttonExistTeamOk);
					btOk.setOnClickListener(new OnClickListener() {
	
						@Override
						public void onClick(View arg0) {
	
							// Variable pour la récupération PHP
					        String joinPHPresult = "";
							
							// Récupération depuis les EditText
							EditText teamNameEdit = (EditText) dialog.findViewById(R.id.editTextExistTeamName);
							EditText memberNameEdit = (EditText) dialog.findViewById(R.id.editTextExistTeamChiefName);
							EditText memberFirstEdit = (EditText) dialog.findViewById(R.id.editTextExistTeamChiefFirstName);
							
							String teamNameStr = Utilities.trimSpaces(teamNameEdit.getText().toString());
							String memberNameStr = Utilities.trimSpaces(memberNameEdit.getText().toString());
							String memberFirstStr = Utilities.trimSpaces(memberFirstEdit.getText().toString());
							
							// Conversion en UTF
							String utf8teamNameStr = "", utf8memberNameStr = "", utf8memberFirstStr = "";
							
							try { utf8teamNameStr = URLEncoder.encode(teamNameStr, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
							try { utf8memberFirstStr = URLEncoder.encode(memberNameStr, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
							try { utf8memberNameStr = URLEncoder.encode(memberFirstStr, "UTF-8"); } catch (UnsupportedEncodingException e1) {};
							
							// Check internet connexion
							if (!isOnline()) {
								DialogError dialErr = new DialogError("Erreur utilisateur", "L'application doit se connecter à Internet, or les connexions de données semblent être désactivées. Veuillez vérifier vos paramètres réseau.", getActivity());
								dialErr.openDialog();
							} 
							// Check values
							else if (teamNameStr.length() == 0 || memberNameStr.length() == 0 || memberFirstStr.length() == 0) {
								DialogError dialErr = new DialogError("Erreur utilisateur", "Impossible de rejoindre l'équipe ! Veuillez vérifier que vous avez correctement complété les informations demandées.", getActivity());
								dialErr.openDialog();
							}
							// Everything's seems okay, let's send it to the server
							else {
							
								// Post sur le serveur	en async
								String contentURL = joinPHP + "?" + "e=" + utf8teamNameStr + "&n=" + utf8memberNameStr + "&p=" + utf8memberFirstStr;
								SimplePostAsyncTask async = new SimplePostAsyncTask(getActivity());
								async.execute(contentURL);
								try { joinPHPresult = async.get(); } catch (InterruptedException e) {} catch (ExecutionException e) {}
								
	
								
								if (joinPHPresult == null) {
									
									DialogError dialErr = new DialogError("Erreur serveur", "Impossible de rejoindre l'équipe : notre serveur est sans doute hors-service. Patientez un peu, nous sommes déjà sur le coup !", getActivity());
									dialErr.openDialog();
									
								} else {
									
									// convert String to Int (try it)
									int resultPHP;
									DialogError dialErr;
									
									try { resultPHP = Integer.parseInt(joinPHPresult); }  catch (NumberFormatException e) { resultPHP = ERROR_UNKNOWN; }
									
									switch (resultPHP) {
									
									case ERROR_UNKNOWN:
										dialErr = new DialogError("Erreur inconnue", "Impossible de rejoindre l'équipe : raison inconnue. Merci de signaler le code d'erreur. (0x" + joinPHPresult + ")", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_JOIN_INEXIST_TEAM:
									case ERROR_JOIN_INEXIST_TEAM | ERROR_JOIN_ALREADY_HERE:
									case ERROR_JOIN_INEXIST_TEAM | ERROR_JOIN_ALREADY_HERE | ERROR_JOIN_ALREADY_OTHER_TEAM:
									case ERROR_JOIN_INEXIST_TEAM | ERROR_JOIN_ALREADY_HERE | ERROR_JOIN_ALREADY_OTHER_TEAM | ERROR_JOIN_FULL_TEAM:
										dialErr = new DialogError("Erreur utilisateur", "Impossible de rejoindre l'équipe, car celle-ci n'existe pas ! Vérifiez les informations saisies, ou créez votre propre équipe ...", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_JOIN_ALREADY_HERE:
									case ERROR_JOIN_ALREADY_HERE | ERROR_JOIN_ALREADY_OTHER_TEAM:
									case ERROR_JOIN_ALREADY_HERE | ERROR_JOIN_FULL_TEAM:
									case ERROR_JOIN_ALREADY_HERE | ERROR_JOIN_ALREADY_OTHER_TEAM | ERROR_JOIN_FULL_TEAM:
										dialErr = new DialogError("Erreur utilisateur", "Impossible de rejoindre l'équipe ... vous êtes déjà dedans. (Tentez vous de tricher ?)", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_JOIN_ALREADY_OTHER_TEAM:
									case ERROR_JOIN_ALREADY_OTHER_TEAM | ERROR_JOIN_FULL_TEAM:
									case ERROR_JOIN_ALREADY_OTHER_TEAM | ERROR_JOIN_INEXIST_TEAM | ERROR_JOIN_FULL_TEAM:
										dialErr = new DialogError("Erreur utilisateur", "Impossible de rejoindre l'équipe ... vous êtes déjà dans une autre équipe. (Tentez vous de tricher ?)", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_JOIN_FULL_TEAM:
									case ERROR_JOIN_FULL_TEAM | ERROR_JOIN_INEXIST_TEAM:
										dialErr = new DialogError("Erreur utilisateur", "Impossible de rejoindre l'équipe, car celle-ci est pleine ! Rejoignez une autre équipe, ou créez la vôtre ...", getActivity());
										dialErr.openDialog();
										break;
										
									case ERROR_NONE:
										
										// Fetch the real team name
										String realTeamName = "---";
										String fetchTeamName = stringToKey(teamNameStr);
										
										// Connect to server
										SimplePostAsyncTask asyncName = new SimplePostAsyncTask(getActivity());
										asyncName.execute(teamNamePHP + "?e=" + fetchTeamName);
										try { realTeamName = asyncName.get(); } catch (InterruptedException e) {} catch (ExecutionException e) {}
										
										// Save Name, and TeamName in preferences
										teamNamePrefs_Write.putBoolean("teamIsChief", false);
										teamNamePrefs_Write.putString("teamName", realTeamName);
										teamNamePrefs_Write.putString("teamUserFirstName", memberFirstStr);
										teamNamePrefs_Write.putString("teamUserName", memberNameStr);
										teamNamePrefs_Write.putBoolean("isInTeam", true);
										teamNamePrefs_Write.commit();
										
										
										AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
										builder.setMessage("Bienvenue " + Utilities.trimSpaces(memberNameStr + ", vous venez de rejoindre l'équipe \"" + realTeamName) + "\" !\n\nBonne chance !")
										       .setTitle("Hey !");
										builder.setCancelable(false);
										builder.setPositiveButton("Commencer à jouer", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface arg0, int arg1) {
												
												Fragment fragment = new TreasurePlay();
												if (fragment != null) {
													FragmentManager fragmentManager = getFragmentManager();
													fragmentManager.beginTransaction()
															.replace(R.id.frame_container, fragment).commit();
			
												}
												
											}										
										});
										
										builder.show();
										break;
									
									}
								}
								
								dialog.dismiss();
							}
							
						}
						
					});
					
					Button btCancel = (Button) dialog.findViewById(R.id.buttonExistTeamCancel);
					btCancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		 
					dialog.show();
				}
			});
	        
        } else {
        	
        	Fragment fragment = new TreasurePlay();
			if (fragment != null) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.frame_container, fragment).commit();

			}
        	
        }
         
        return rootView;
    }
	
	// Format a string into a key
	// no spaces, accents, only utf7 caracters
	public String stringToKey (String str) {
		
		String alphaAndDigits = str.replaceAll("[^a-zA-Z0-9]","");
		
		return alphaAndDigits;
	}
	
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    return netInfo != null && netInfo.isConnectedOrConnecting();
	}
}
