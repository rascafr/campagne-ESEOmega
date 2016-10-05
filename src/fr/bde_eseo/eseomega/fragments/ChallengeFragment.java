package fr.bde_eseo.eseomega.fragments;

import fr.bde_eseo.eseomega.R;
import fr.bde_eseo.eseomega.adapter.MyChallengeAdapter;
import fr.bde_eseo.eseomega.myObjects.ChallengeObject;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * Page permettant de voir les challenges proposées ainsi que de les valider.
 * @author François
 *
 */
public class ChallengeFragment extends Fragment  {
	
	MyChallengeAdapter mAdapter;
	ListView mListView;
	SharedPreferences teamNamePrefs_Read;
	SharedPreferences.Editor teamNamePrefs_Write;
	LayoutInflater inflater;
	
	// Les catégories
	private static String [] challengesCat = {"Les défis d'Athéna", "Top Cronos", "L'ambroisie des dieux", "Artémis à la chasse", "Les plaisirs d'Hermès", "Les supplices d'Hadès", "Les folies d'Apollon", "Zeus à l'ESEO", "Les caprices d'Hestia", "La vengeance de Poséidon", "Les délires de Cupidon", "Dionysos paie sa tournée"};
	
	// Les noms des challenges
	private static String [][] challengesNames = {{"Défis QRcodes", "Le snap/photo ressemblant le plus\nà un grec/dieu", "Le plus de monde sur une chaise", "Télécharger l'app ESEOmega", "Liker la page Facebook de la liste", "Ajouter la liste sur Snapchat"},
        {"Lancer un aviron bayonnais (paquito) à Ralliement", "Danser YMCA sur la fontaine du Ralliement : torse nu", "Danser YMCA sur la fontaine du Ralliement : en soutif", "Pyramide humaine à au moins 6 à Ralliement", "Course contre le tram entre Ralliement et Molière"},
        {"Manger un sandwich de pâté au chien/chat", "Gober un œuf", "Manger 5 biscuits pour chien", "Gober 5 flans"},
        {"Boire tout seul 33 cL de Coca cul sec", "Boire un café/verre d’eau salé(e)", "Tenir un glaçon dans sa main jusqu’à ce qu’il fonde"},
        {"Faire une commande parfaite à McDo", "Poser dans une vitrine de magasin avec une tenue de grec", "Passer au Drive à pied en imitant la voiture", "Aller chercher des échantillons gratuits dans un sex-shop"},
        {"Se faire épiler les aisselles à la cire", "Se laver les dents au tabasco", "Snifer un rail de poivre"},
        {"Chanter une chanson paillarde à la BU (St Serge ou Belle-Beille)", "Réussir à refaire la chorégraphie de la liste", "Chanter le Célestin dans le tram"},
        {"Avoir un autographe d'une fille de la liste sur le derrière", "Faire le lèche-cul auprès de Billy", "Demander les numéros de la liste (50 %)", "Venir en sandales/chaussettes à l’ESEO", "Prendre un selfie avec le Directeur\nou M. Madeline"},
        {"Embrasser (bouche, avec/sans langue) 3 inconnu(e)s dans la rue", "« Je te tiens, tu me tiens »\navec un ASVP/policier", "Arroser un inconnu"},
        {"Se laver au lavage auto au Karcher : habillé(e)", "Se laver au lavage auto au Karcher : en sous-vêtements", "Se laver au lavage auto au Karcher : à 3", "Se laver au lavage auto au Karcher : nu(e)", "Se laver les cheveux à la farine", "Laver le pare-brise de 3 voitures à un feu rouge"},
        {"Lécher les aisselles d’un mec poilu", "Se mettre une capote sur la tête jusqu’au nez et réussir à la gonfler", "Réaliser une prise du Kâmasûtra"},
        {"Boire une girafe à 2", "Réussir à se faire payer un Ricard par le président de la liste", "Une pinte cul sec", "Une bière micro-ondes (25 cL, chaude)"}};
	
	// Les règles des challenges
	private static String [][] challengesRules = {{"voir onglet \"Chasse QRcodes\" ...", "libre", "libre", "libre", "libre", "libre"},
						        {"vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire", "photo nécessaire", "vidéo nécessaire"},
						        {"vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire"},
						        {"vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire"},
						        {"vidéo nécessaire", "photo nécessaire", "vidéo nécessaire", "nous les apporter"},
						        {"vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire"},
						        {"vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire"},
						        {"photo avec la fille", "à voir avec Billy", "nous les apporter", "photo nécessaire", "photo nécessaire"},
						        {"vidéo nécessaire", "photo nécessaire", "vidéo nécessaire"},
						        {"vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire", "vidéo nécessaire", "photo nécessaire", "vidéo nécessaire"},
						        {"photo nécessaire", "photo nécessaire", "photo nécessaire"},
						        {"vidéo nécessaire", "photo nécessaire", "vidéo nécessaire", "vidéo nécessaire"}};
	
	// Les points des challenges
	private static String [][] defisPts  = {{"QRC", "15", "15", "3", "3", "3"},
								            {"9", "3", "6", "6", "3"},
								            {"9", "6", "3", "3"},
								            {"9", "3", "3"},
								            {"9", "6", "6", "6"},
								            {"12", "9", "6"},
								            {"12", "6", "3"},
								            {"12", "12", "9", "6", "6"},
								            {"9", "9", "3"},
								            {"6", "9", "9", "12", "6", "6"},
								            {"9", "6", "3"},
								            {"12", "12", "3", "3"}};
	
	// Consignes des challenges
	private static String instructions = "<br>- Inscrivez-vous et formez un groupe de 4 personnes dont un chef, et tentez de réaliser le plus de défis possibles !<br><br>" +
											"- Chaque défi, dans chaque thème, rapporte plus ou moins de points<br><br>" + 
											"- Selon la mission, il est nécessaire d'envoyer une preuve par photo ou vidéo sur Facebook ou à bde@eseomega.fr)<br><br>" +
											"- Pour vous aider, vous pouvez cocher ici ce que vous avez déjà réalisé, le cumul des points se fera automatiquement<br><br>" + 
											"- L'équipe gagnante, celle qui aura le plus de points et de QRcodes flashés, sera bien récompensée !<br><br>" +
											"<b>Bon courage !</b><br>";
	
	public ChallengeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflaterR, ViewGroup container,
            Bundle savedInstanceState) {
 
		// Get Views
		inflater = inflaterR;
        View rootView = inflater.inflate(R.layout.fragment_challenges, container, false);
        final RelativeLayout layInfo = (RelativeLayout) rootView.findViewById(R.id.layoutChallengesInfo);
        final TextView tvPointsSum = (TextView) rootView.findViewById(R.id.challengesPoints);
        
        // Get parameters from preferences
 		teamNamePrefs_Read = getActivity().getSharedPreferences(TreasureMenu.PREFS_NAME_TEAM, 0);
 		teamNamePrefs_Write = teamNamePrefs_Read.edit();
 		
        /** Gestion de la ListView des défis 3 jours **/
        
        // Initialisation de la listView
        mListView = (ListView) rootView.findViewById(R.id.listChallenges);
        mAdapter = new MyChallengeAdapter(getActivity());
        
        // Ajout des éléments
        // TO DO : chargement des données -> OK (préférences)
        int color, pos = 0;
        String tempPoints;
        boolean isDone = false;
        
        for (int i=0;i<challengesCat.length;i++) {
        	mAdapter.addChallengeCategory(challengesCat[i]); pos++; // because category has a position
        	color = ChallengeObject.FLAT_COLORS[i%ChallengeObject.FLAT_COLORS.length];
        	for (int j=0;j<challengesNames[i].length;j++) {
        		
        		isDone = teamNamePrefs_Read.getBoolean("challenge"+pos, false); pos++; // update postion counter
        		tempPoints = defisPts[i][j];
        		
        		if (i==0 && j==0) { // QRcodes
        			tempPoints = teamNamePrefs_Read.getString(TreasurePlay.PREFS_KEY_POINTS, "0");
        			if (tempPoints.length() == 0) tempPoints = "0"; // team has been deleted / server error
        		} else if (i==0 & j==3) { // Télécharger l'appli ESEOmega
        			isDone = true;
        		}
        		 
        		mAdapter.addChallengeItem(challengesNames[i][j], challengesRules[i][j], tempPoints, color, isDone);
        	}
        }
        tvPointsSum.setText(calculatePoints() + " pts");
        
        // On définit l'adaptateur
        mListView.setAdapter(mAdapter);
        
        // Lors d'un clic -> validation -> recalcul des points
        mListView.setOnItemClickListener(new OnItemClickListener() {
			
         	public void onItemClick(AdapterView<?> a, View v, int position, long id) {
         		
         		if (mAdapter.getItemViewType(position) == MyChallengeAdapter.TYPE_CHALLENGE) {

	        		mAdapter.toggleChallengeAchievement(position);
	        		teamNamePrefs_Write.putBoolean("challenge"+position, mAdapter.getObject(position).getAchievement()); // save achievement
	        		teamNamePrefs_Write.commit();
	        		tvPointsSum.setText(calculatePoints() + " pts");
         		} else {
         			mListView.smoothScrollToPositionFromTop(position, 0);
         		}
        	}
         });
        
        // On clic on about -> change to listener onTouch
        /*layInfo.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				Log.d("Motion", "action = " + event.getAction());
				
				
				switch (event.getAction()) {
				
				// Pressed	
				case MotionEvent.ACTION_DOWN:
				
					layInfo.setBackgroundColor(Color.DKGRAY);
					
					
					break;
					
				// Released
				case MotionEvent.ACTION_UP:
					layInfo.setBackgroundColor(getResources().getColor(R.color.list_background));
					
					break;
				
				}
				
				return false;
			}
		});*/
        
    	layInfo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle("Instructions de jeu");
					
					View vDialog = inflater.inflate(R.layout.custom_layout_text, null);
					
					builder.setView(vDialog);
					
					TextView tvTxt = (TextView) vDialog.findViewById(R.id.txt);
					tvTxt.setText(Html.fromHtml(instructions));
					
					builder.setNegativeButton("Fermer", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}										
					});
					builder.show();
				
			}
		});
        
        return rootView;
    }
	
	private int calculatePoints () {
		
		int sum = 0;
		String sPts;
		
		for (int i=0;i<mAdapter.getCount();i++) {
			// TO DO : calculate sum with Treasure points saved in preferences -> OK
			if (mAdapter.getObject(i).getAchievement() && mAdapter.getItemViewType(i) == MyChallengeAdapter.TYPE_CHALLENGE) { // is checked
				sPts = mAdapter.getObject(i).getPoints();
				if (sPts.equalsIgnoreCase("-")) { // Billy's anim
					
				} else { // Real String points
					sum += Integer.parseInt(sPts);
				}
			}
		}
		
		return sum;		
	}
}
