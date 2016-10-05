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
 * Page permettant de voir les challenges propos�es ainsi que de les valider.
 * @author Fran�ois
 *
 */
public class ChallengeFragment extends Fragment  {
	
	MyChallengeAdapter mAdapter;
	ListView mListView;
	SharedPreferences teamNamePrefs_Read;
	SharedPreferences.Editor teamNamePrefs_Write;
	LayoutInflater inflater;
	
	// Les cat�gories
	private static String [] challengesCat = {"Les d�fis d'Ath�na", "Top Cronos", "L'ambroisie des dieux", "Art�mis � la chasse", "Les plaisirs d'Herm�s", "Les supplices d'Had�s", "Les folies d'Apollon", "Zeus � l'ESEO", "Les caprices d'Hestia", "La vengeance de Pos�idon", "Les d�lires de Cupidon", "Dionysos paie sa tourn�e"};
	
	// Les noms des challenges
	private static String [][] challengesNames = {{"D�fis QRcodes", "Le snap/photo ressemblant le plus\n� un grec/dieu", "Le plus de monde sur une chaise", "T�l�charger l'app ESEOmega", "Liker la page Facebook de la liste", "Ajouter la liste sur Snapchat"},
        {"Lancer un aviron bayonnais (paquito) � Ralliement", "Danser YMCA sur la fontaine du Ralliement : torse nu", "Danser YMCA sur la fontaine du Ralliement : en soutif", "Pyramide humaine � au moins 6 � Ralliement", "Course contre le tram entre Ralliement et Moli�re"},
        {"Manger un sandwich de p�t� au chien/chat", "Gober un �uf", "Manger 5 biscuits pour chien", "Gober 5 flans"},
        {"Boire tout seul 33 cL de Coca cul sec", "Boire un caf�/verre d�eau sal�(e)", "Tenir un gla�on dans sa main jusqu�� ce qu�il fonde"},
        {"Faire une commande parfaite � McDo", "Poser dans une vitrine de magasin avec une tenue de grec", "Passer au Drive � pied en imitant la voiture", "Aller chercher des �chantillons gratuits dans un sex-shop"},
        {"Se faire �piler les aisselles � la cire", "Se laver les dents au tabasco", "Snifer un rail de poivre"},
        {"Chanter une chanson paillarde � la BU (St Serge ou Belle-Beille)", "R�ussir � refaire la chor�graphie de la liste", "Chanter le C�lestin dans le tram"},
        {"Avoir un autographe d'une fille de la liste sur le derri�re", "Faire le l�che-cul aupr�s de Billy", "Demander les num�ros de la liste (50 %)", "Venir en sandales/chaussettes � l�ESEO", "Prendre un selfie avec le Directeur\nou M. Madeline"},
        {"Embrasser (bouche, avec/sans langue) 3 inconnu(e)s dans la rue", "��Je te tiens, tu me tiens �\navec un ASVP/policier", "Arroser un inconnu"},
        {"Se laver au lavage auto au Karcher : habill�(e)", "Se laver au lavage auto au Karcher : en sous-v�tements", "Se laver au lavage auto au Karcher : � 3", "Se laver au lavage auto au Karcher : nu(e)", "Se laver les cheveux � la farine", "Laver le pare-brise de 3 voitures � un feu rouge"},
        {"L�cher les aisselles d�un mec poilu", "Se mettre une capote sur la t�te jusqu�au nez et r�ussir � la gonfler", "R�aliser une prise du K�mas�tra"},
        {"Boire une girafe � 2", "R�ussir � se faire payer un Ricard par le pr�sident de la liste", "Une pinte cul sec", "Une bi�re micro-ondes (25 cL, chaude)"}};
	
	// Les r�gles des challenges
	private static String [][] challengesRules = {{"voir onglet \"Chasse QRcodes\" ...", "libre", "libre", "libre", "libre", "libre"},
						        {"vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire", "photo n�cessaire", "vid�o n�cessaire"},
						        {"vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire"},
						        {"vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire"},
						        {"vid�o n�cessaire", "photo n�cessaire", "vid�o n�cessaire", "nous les apporter"},
						        {"vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire"},
						        {"vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire"},
						        {"photo avec la fille", "� voir avec Billy", "nous les apporter", "photo n�cessaire", "photo n�cessaire"},
						        {"vid�o n�cessaire", "photo n�cessaire", "vid�o n�cessaire"},
						        {"vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire", "photo n�cessaire", "vid�o n�cessaire"},
						        {"photo n�cessaire", "photo n�cessaire", "photo n�cessaire"},
						        {"vid�o n�cessaire", "photo n�cessaire", "vid�o n�cessaire", "vid�o n�cessaire"}};
	
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
	private static String instructions = "<br>- Inscrivez-vous et formez un groupe de 4 personnes dont un chef, et tentez de r�aliser le plus de d�fis possibles !<br><br>" +
											"- Chaque d�fi, dans chaque th�me, rapporte plus ou moins de points<br><br>" + 
											"- Selon la mission, il est n�cessaire d'envoyer une preuve par photo ou vid�o sur Facebook ou � bde@eseomega.fr)<br><br>" +
											"- Pour vous aider, vous pouvez cocher ici ce que vous avez d�j� r�alis�, le cumul des points se fera automatiquement<br><br>" + 
											"- L'�quipe gagnante, celle qui aura le plus de points et de QRcodes flash�s, sera bien r�compens�e !<br><br>" +
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
 		
        /** Gestion de la ListView des d�fis 3 jours **/
        
        // Initialisation de la listView
        mListView = (ListView) rootView.findViewById(R.id.listChallenges);
        mAdapter = new MyChallengeAdapter(getActivity());
        
        // Ajout des �l�ments
        // TO DO : chargement des donn�es -> OK (pr�f�rences)
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
        		} else if (i==0 & j==3) { // T�l�charger l'appli ESEOmega
        			isDone = true;
        		}
        		 
        		mAdapter.addChallengeItem(challengesNames[i][j], challengesRules[i][j], tempPoints, color, isDone);
        	}
        }
        tvPointsSum.setText(calculatePoints() + " pts");
        
        // On d�finit l'adaptateur
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
