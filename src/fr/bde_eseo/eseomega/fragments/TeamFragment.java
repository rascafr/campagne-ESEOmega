package fr.bde_eseo.eseomega.fragments;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.adapter.MyTeamAdapter;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Permet l'affichage des membres de la liste du BDE.
 * @author Fran�ois
 *
 */
public class TeamFragment extends Fragment  {
	
	ListView mListView;
	MyTeamAdapter mAdapter;
	ArrayList<HashMap<String, String>> listItem;
	HashMap<String, String> map;
	static int maxItemsTeam = 6;
	static int compteur_jeje = 0;
	
	private static String[] headers = { "Bureau", 
										"Event", 
										"Anim", 
										"Club", 
										"Com", 
										"REX/Interpole", 
										"Log", 
										"RCII", 
										"Voyage", 
										"Sponsors"};

	
	private static String[][] humans = {{"J�r�my Br�e", "Marine Icard", "Rodolphe Dubant", "Joseph Hien", "Romain Kermorvant", "C�cile Delage"},
										{"Romain Mesnil", "Marwan Boughammoura", "Samia Charaa", "Alexis Demay", "Pierre Flouvat-Cavier", "Julie Frichet", "Eva Legrand", "Flavien Reynaud"},
										{"Yoann Beuch�", "Arnaud Billy", "Antoine Bret�cher", "Aur�lien Clause", "Cl�ment Letailleur", "Lo�ck Planchenault"},
										{"Ludivine Leal", "Nicolas Basily", "In�s Deliaire", "Marie Quervarec"},
										{"Axel Cahier", "Tim� Kadel", "Sonasi Katoa", "Fran�ois Leparoux", "Alexis Louis", "Thomas Naudet", "Axel Rollo"},
										{"Victor Voirand", "Perrine Blaudet", "Victoria Louboutin"},
										{"Baudouin de Miniac", "Baptiste Gouesbet", "Antoine de Pouilly"},
										{"Alexandre Cosneau", "Margaux Blanchard", "Ana�s Crosnier"},
										{"�lodie Boiteux", "Isabelle Baudvin"},
										{"�lise Habib", "Jean Hardy", "Nicolas Lign�e"}};
	
	private static String[][] descriptions = {{  "Pr�sident", "Vice-pr�sidente", "Vice-pr�sident", "Tr�sorier", "Tr�sorier", "Secr�taire"},
										        {"Responsable / S�cu", "Staff s�cu", "Staff", "Staff", "Staff", "Staff", "Staff", "Staff"},
										        {"Responsable", "Staff", "Staff", "Staff", "Staff", "Staff"},
										        {"Responsable", "Staff", "Staff", "Staff"},
										        {"Responsable", "Staff", "Staff #social #kfc", "D�veloppeur Android", "Staff", "D�veloppeur iOS", "Staff"},
										        {"Responsable", "Staff", "Staff"},
										        {"Responsable", "Staff", "Staff", "Staff"},
										        {"Responsable", "Staff", "Staff"},
										        {"Responsable", "Staff"},
										        {"Responsable", "Staff", "Staff"}};
	
	public TeamFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
        View rootView = inflater.inflate(R.layout.fragment_team_presentation, container, false);
        
    	/** Gestion de la ListView des animations pr�vues **/
        
        // Initialisation de la listView
        mAdapter = new MyTeamAdapter(getActivity());
        
        String dirModule;
        
        // On balaie les modules
        for (int m=0;m<headers.length;m++) {
        	
        	// On ajoute chaque module � notre view
        	mAdapter.addHeaderItem(headers[m]);
        	
        	// v2.0 : module + 0.... inf
    		dirModule = Normalizer.normalize(headers[m].toLowerCase(Locale.FRANCE), Normalizer.Form.NFD);
    		dirModule = headers[m].toLowerCase(Locale.FRANCE).replaceAll("[^\\p{ASCII}]", "");
    		dirModule = dirModule.replace("/", "");
    		
        	// On balaie les personnes de ce module
        	for (int h=0;h<humans[m].length;h++) {
        		
        		// On ajoute chaque personne (humain H du module M) � notre view
        		// Lien de l'image : assets/"moduleiD"/"humaniD".jpg
        		//mAdapter.addHumanItem(humans[m][h], "Humain n�" + h + " du module \"" + headers[m] + "\"", m + "/" + h);
        		
        		mAdapter.addHumanItem(humans[m][h], descriptions[m][h], dirModule + "/" + h);
        		//mAdapter.addHumanItem(humans[m][h], descriptions[m][h], m + "/" + h);
        	}
        }
        
        // On trouve la listView, on ajoute les donn�es dedans
        mListView = (ListView) rootView.findViewById(R.id.listTeam);
        mListView.setAdapter(mAdapter);
        
        //Enfin on met un �couteur d'�v�nement sur notre listView
        mListView.setOnItemClickListener(new OnItemClickListener() {
			
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
         		
         		// Clic sur un header -> position � cet endroit
         		if (mAdapter.getItemViewType(position) == MyTeamAdapter.TYPE_HEADER) {
         			mListView.smoothScrollToPositionFromTop(position, 0);
         		} else {
         			if (position == 1) {
         				if (compteur_jeje < 5) compteur_jeje ++;
         				else if (compteur_jeje == 5){
         				
         			        final Dialog dialog = new Dialog(getActivity());
         					dialog.setContentView(R.layout.custom_dialog_easter);
         					ImageView img = (ImageView) dialog.findViewById(R.id.pictureEaster); // Photo de l'event
            		    	try {
            					img.setImageDrawable(Drawable.createFromStream(getActivity().getAssets().open("easteregg/fragile.jpg"), null));
            				} catch (IOException e) {}
         					dialog.setTitle("Easter egg");
         					dialog.show();
         					//Toast.makeText(getActivity(), "Bite", Toast.LENGTH_LONG).show();
         					compteur_jeje++;
         				}
         			}
         		}
        	}
         });
         
        return rootView;
    }
}
