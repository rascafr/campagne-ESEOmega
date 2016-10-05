package fr.bde_eseo.eseomega.fragments;

import fr.bde_eseo.eseomega.R;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Page d'affichage quand le jeu n'a pas commencé ou est terminé.
 * @author François
 *
 */
public class TreasurePause extends Fragment  {
	
	private boolean playDaysAreOver;

public TreasurePause(boolean playDaysAreOver){
	this.playDaysAreOver = playDaysAreOver;
}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		       
		// Inflate view and elements
		View rootView = inflater.inflate(R.layout.fragment_treasure_final, container, false);
		TextView tvInfo = (TextView) rootView.findViewById(R.id.treasureMessage);
		TextView tvMemberName = (TextView) rootView.findViewById(R.id.treasureMemberFullName);
		TextView tvTeamName = (TextView) rootView.findViewById(R.id.treasureTeamName);
		TextView tvTeamPoints = (TextView) rootView.findViewById(R.id.treasureTeamPoints);
		ImageView imgStar1 = (ImageView) rootView.findViewById(R.id.star1);
		ImageView imgStar2 = (ImageView) rootView.findViewById(R.id.star2);
		ImageView imgStar3 = (ImageView) rootView.findViewById(R.id.star3);
		
		// Images default
		imgStar1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grade_grey));
		imgStar2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grade_grey));
		imgStar3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grade_grey));
		
		// Préférences
        SharedPreferences teamNamePrefs_Read = getActivity().getSharedPreferences(TreasureMenu.PREFS_NAME_TEAM, 0);
        
        boolean teamHasBeenRegistered = teamNamePrefs_Read.getBoolean("isInTeam", false);
		
		// User not registred in team -> 
		if (!teamHasBeenRegistered) {
			
			if (playDaysAreOver) {
				tvInfo.setText("Le jeu est terminé !\n Dommage, vous n'y avez pas participé ...");
				tvMemberName.setText(":(");
				tvTeamName.setText("");
			} else {
				tvInfo.setText("");
				tvMemberName.setText("Le jeu n'a pas encore commencé !");
				tvTeamName.setText("Revenez lundi matin ...");
			}
		
			tvTeamPoints.setText("");
			imgStar1.setVisibility(View.INVISIBLE);
			imgStar2.setVisibility(View.INVISIBLE);
			imgStar3.setVisibility(View.INVISIBLE);
			
		} else {
			
			// User registered but play days are done
			tvInfo.setText("Le jeu est terminé !\n Merci de votre participation.\nVotre résumé :");
			tvMemberName.setText(teamNamePrefs_Read.getString("teamUserFirstName", "Erreur") + " " + teamNamePrefs_Read.getString("teamUserName", "Erreur"));
			tvTeamName.setText(teamNamePrefs_Read.getString("teamName", "Erreur"));
			String points = teamNamePrefs_Read.getString(TreasurePlay.PREFS_KEY_POINTS, "0");
			int pointsInt = 0;
			
			try { pointsInt = Integer.parseInt(points); } catch (NumberFormatException e) { pointsInt = 0; }
			tvTeamPoints.setText(pointsInt + " pts");
			
			if (pointsInt > 18) imgStar1.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grade_white));
			if (pointsInt > 37) imgStar2.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grade_white));
			if (pointsInt > 55) imgStar3.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_grade_white));
		
		
		}
		
		return rootView;
	}
	
	/*
	 * 
				imgStar1.setVisibility(View.INVISIBLE);
				imgStar2.setVisibility(View.INVISIBLE);
				imgStar3.setVisibility(View.INVISIBLE);
	 */
}
